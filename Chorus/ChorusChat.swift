//
//  ChorusChat.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/12/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit
import Alamofire
import CoreData

class ChorusChat: UITableViewController, NSURLConnectionDelegate, SpeechKitDelegate, SKVocalizerDelegate, UITextFieldDelegate, UITableViewDataSource {
    
    let url: String = "https://talkingtothecrowd.org/Chorus/Chorus-New/"
    let chatURL: String
    var data: NSMutableData = NSMutableData()
    var cli_coredata = [NSManagedObject]() //Core Data
    var vocalizer: SKVocalizer?
    var caller: String = String()
    var can_update: Bool = Bool()
    
    var chatLineInfo: ChatLineInfo = ChatLineInfo()
    var cli_list: [ChatLineInfo] = [ChatLineInfo]()
    var chat_list: [String] = [String]()
    var task: String = String()
    var role: String = String() //who is looking at Chorus Chat
    var chatLine: String = String()
    
    required init!(coder aDecoder: NSCoder!) {
        self.chatURL = url + "php/chatProcess.php"
        
        super.init(coder: aDecoder)
    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!) {
        self.data.appendData(data)
    }
    
    //MARK: Actions
    @IBAction func handleTap(sender: UITapGestureRecognizer) {
        //text to speech
        vocalizer = SKVocalizer(language: "eng-USA", delegate: self)
        //vocalizer?.speakString("test")
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let currentCell = tableView.cellForRowAtIndexPath(indexPath) as UITableViewCell!;
        var cellTitle = currentCell.textLabel!.text
        
        //remove requester and extract chatLine from cell title
        var colon = cellTitle!.rangeOfString(":")!.startIndex
        cellTitle = cellTitle!.substringFromIndex(advance(colon, 1))
        
        vocalizer = SKVocalizer(language: "eng-USA", delegate: self)
        vocalizer?.speakString(cellTitle)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        self.navigationController?.setToolbarHidden(true, animated: false) //hide bottom toolbar
        self.can_update = true //will recursively update

        if(self.task == "") { //temporary for login
            self.task = "6"
        }
        if(self.role == "") {
            self.role = "requester"
        }
        
        //MARK: initially fill chatlines
        //check if core data is empty or not
        //1. access NSManagedObjectContext
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        //2. fetch Core Data
        let fetchRequest = NSFetchRequest(entityName: "ChatLineInfo")
        //3. handle error from fetching request
        var error: NSError?
        let fetchedResults = managedContext.executeFetchRequest(fetchRequest, error: &error) as? [NSManagedObject]
        if let results = fetchedResults {
            if(results.count == 0) { //empty core data -> pull from web
                println("web")
                self.setChatLinesFromWeb()
            }
            else { //filled core data -> pull from core data
                println("core data")
                self.setChatLinesFromCD()
            }
        }
        else {
            if(error != nil) {
                self.error(error!.description)
            }
        }
        
        //SpeechKit setup
        //NON SSL Host: sandbox.nmdp.nuancemobility.net, Port: 443
        //SSL Host: sslsandbox.nmdp.nuancemobility.net , Port: 443
        //AppID: NMDPTRIAL_sckpeace519_gmail_com20150816112650
        /*AppKey: 0x1e 0x51 0xa4 0x95 0xa0 0x4d 0x90 0xaf 0x14 0xad 0x42 0xd6 0xba 0x28 0x03 0x83 0xb8 0x14 0x4e 0x15 0xc7 0xdb 0x2f 0xc8 0x6d 0xcc 0x97 0x5a 0x60 0xed 0x97 0x7e 0x3f 0x3c 0x13 0xdf 0x89 0xa3 0x8e 0x9e 0x51 0xd0 0x74 0x0b 0xf8 0x77 0x8f 0xb0 0x8b 0xdd 0xc5 0x53 0xb9 0xf4 0x1b 0x26 0xc0 0xb2 0x80 0x20 0x9f 0x18 0x9f 0xde
        */
        SpeechKit.setupWithID("NMDPTRIAL_sckpeace519_gmail_com20150816112650", host: "sandbox.nmdp.nuancemobility.net", port: 443, useSSL: false, delegate: self)

        if(caller == "embedded" || caller == "speech") {
            self.postData(self.chatLine, _task: self.task)
        }
    }
    
    override func viewDidAppear(animated: Bool) {
        //MARK: setup navigation bar
        //put logo in navigation bar
        var nav = self.navigationController?.navigationBar
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.blueColor()
        
        //Chorus logo image title
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 100, height: 40))
        imageView.contentMode = .ScaleAspectFit
        
        let image = UIImage(named: "ChorusLogo") //load image
        imageView.image = image //set image to imageView
        
        //set title of navigation bar to image
        navigationItem.titleView = imageView
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Potentially incomplete method implementation.
        // Return the number of sections.
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete method implementation.
        // Return the number of rows in the section.
        return chat_list.count
    }
    
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell
    {
        let cell = tableView.dequeueReusableCellWithIdentifier("ChatLine", forIndexPath: indexPath) as! UITableViewCell
        cell.textLabel?.text = self.chat_list[indexPath.row]
        return cell
    }
    
    func error(message: String) -> Void {
        var alert = UIAlertController(title: "Error", message: message, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Okay", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func scrollToBottom() {
        //scroll to the bottom of list
        let numberOfSections = self.tableView.numberOfSections()
        let numberOfRows = self.tableView.numberOfRowsInSection(numberOfSections - 1)
        if(numberOfRows > 0) {
            let indexPath = NSIndexPath(forRow: numberOfRows - 1, inSection: numberOfSections - 1)
            self.tableView.scrollToRowAtIndexPath(indexPath, atScrollPosition: UITableViewScrollPosition.Bottom, animated: true)
        }
    }
    //MARK: Alamofire
    func parseCLI(lineInfo: [String], target: String) -> String {
        for (var index = 1; index < lineInfo.count; index+=4)  {
            switch lineInfo[index] {
            case target:
                return lineInfo[index+2]
            default:
                break
            }
        }
        return ""
    }
    func inputCoreData(_chatid: String, _message: String, _role: String, _task: String, _time: String) -> Void {
        //update ChatLineInfo Core Data
        //1. access NSManagedObjectContext
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        //2. create new managed object and insert it into managed object context
        let cli_entity = NSEntityDescription.entityForName("ChatLineInfo", inManagedObjectContext: managedContext)
        let cli = NSManagedObject(entity: cli_entity!, insertIntoManagedObjectContext: managedContext)
        //3. key-value coding
        cli.setValue(_chatid, forKey: "chatid")
        cli.setValue(_message, forKey: "message")
        cli.setValue(_role, forKey: "role")
        cli.setValue(_task, forKey: "task")
        cli.setValue(_time, forKey: "time")
        //4. handling error when saving new chatlineinfo to core data
        var error: NSError?
        if !managedContext.save(&error) {
            if(error != nil) {
                self.error(error!.description)
            }
        }
        //5. insert new managed object to core data
        cli_coredata.append(cli)
    }
    func setChatInfo(data: String) -> Void {
        //parsing results from server into chatLineInfo's
        let array = split(data){$0 == "}"}.map{String($0)}
        for (var i = 0; i < array.count-1; i++) {
            //add new chatLineInfo
            //let lineInfo = split(array[array.count-2]){$0 == "\""}.map{String($0)}
            let lineInfo = split(array[i]){$0 == "\""}.map{String($0)}
            let cli_temp: ChatLineInfo = chatLineInfo.setChatLineInfo(lineInfo, chatLineInfo: ChatLineInfo())
    
            if let chatLineStart = array[i].rangeOfString("chatLine\":\"")?.startIndex {
                if let roleStart = array[i].rangeOfString("\",\"role")?.startIndex {
                    let temp_chatLine = array[i].substringWithRange(Range<String.Index>(start: advance(chatLineStart, 11), end: roleStart))
                    chatLineInfo.set_chatLine(temp_chatLine)
                    
                }
                else {
                    error("Cannot find role.")
                }
            }
            else {
                error("Cannot find start of chat line.")
            }
            
            //fill chat list
            cli_list.append(cli_temp)
            if(cli_temp.get_role() == "requester") {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_acceptedTime())")
                self.inputCoreData(self.parseCLI(self.chat_list, target: "id"), _message: self.parseCLI(self.chat_list, target: "chatLine"), _role: self.parseCLI(self.chat_list, target: "role"), _task: self.parseCLI(self.chat_list, target: "task"), _time: self.parseCLI(self.chat_list, target: "time"))
            }
            else {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_time())")
                self.inputCoreData(self.parseCLI(self.chat_list, target: "id"), _message: self.parseCLI(self.chat_list, target: "chatLine"), _role: self.parseCLI(self.chat_list, target: "role"), _task: self.parseCLI(self.chat_list, target: "task"), _time: self.parseCLI(self.chat_list, target: "time"))
            }
        }
        self.tableView.reloadData() //update tableview
        self.scrollToBottom() //scroll to bottom of list
    }
    func setChatLinesFromWeb() {
        //Pull from Chorus server and update chat page
        Alamofire.request(.GET, NSURL(string: chatURL)!, parameters: ["action" : "fetchNewChatRequester", "role": self.role, "task": self.task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": "-1"]).responseString(encoding: NSUTF8StringEncoding, completionHandler: { (_, _, result, error) in
            if(result != nil) {
                self.setChatInfo(result!)
            }
            
            //popup error alert
            if(error != nil) {
                self.error(error!.description)
            }
        })
        self.scrollToBottom() //scroll to bottom of list
        if(can_update) {
            self.update()
        }
    }
    func setChatLinesFromCD() {
        //1. access NSManagedObjectContext
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        //2. fetch Core Data
        let fetchRequest = NSFetchRequest(entityName: "ChatLineInfo")
        //3. handle error from fetching request
        var error: NSError?
        let fetchedResults = managedContext.executeFetchRequest(fetchRequest, error: &error) as? [NSManagedObject]
        if let results = fetchedResults {
            self.cli_coredata = results
        }
        else {
            if(error != nil) {
                self.error(error!.description)
            }
        }
        self.scrollToBottom()
        if(can_update) {
            self.update()
        }
    }
    func postData(message: String, _task: String) -> Void {
        //Post data to Chorus server. role = "requester"
        Alamofire.request(.POST, NSURL(string: chatURL)!, parameters: ["action" : "post", "role": "requester", "task": _task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": "\0", "chatLine": message]).responseString(encoding: NSUTF8StringEncoding, completionHandler: {(_, _, result, error) in
            if(error != nil) {
                self.error(error!.description)
            }})
        
        //TO DO: FIX update tableview
        if(self.role == "") {
            self.role = "requester"
        }
        if(self.task == "") { //temporary for login
            self.task = "6"
        }
        self.scrollToBottom()
        if(can_update) {
            self.update()
        }
    }
    func update() {
        //Pull from Chorus server and update chat page
        Alamofire.request(.GET, NSURL(string: chatURL)!, parameters: ["action" : "fetchNewChatRequester", "role": self.role, "task": self.task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": self.cli_list[self.cli_list.count-2].get_id()]).responseString(encoding: NSUTF8StringEncoding, completionHandler: { (_, _, result, error) in
            if(result != nil) {
                if(result != "") {
                    self.setChatInfo(result!)
                }
            }
            
            //popup error alert
            if(error != nil) {
                self.error(error!.description)
            }
        })
        self.scrollToBottom() //scroll to bottom of list
        if(can_update == true) {
            update()
        }
    }
    
    //MARK: SpeechKit (text to speech)
    func vocalizer(vocalizer: SKVocalizer!, willBeginSpeakingString text: String!) {
        //text is being spoken
        println("speaking")
    }
    func vocalizer(vocalizer: SKVocalizer!, didFinishSpeakingString text: String!, withError error: NSError!) {
        //there was an error
        if(error != nil) {
            self.error(error!.description)
        }
    }
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
    // Return NO if you do not want the specified item to be editable.
    return true
    }
    */
    
    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
    if editingStyle == .Delete {
    // Delete the row from the data source
    tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
    } else if editingStyle == .Insert {
    // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }
    }
    */
    
    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {
    
    }
    */
    
    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
    // Return NO if you do not want the item to be re-orderable.
    return true
    }
    */
    
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
        self.can_update = false //stop recursive updating
    }

    
}

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
                println("web setup")
                self.setChatLinesFromWeb()
            }
            else { //filled core data -> pull from core data
                println("core data setup")
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
        
        if(caller == "embedded") {
            self.postData(self.chatLine, _task: self.task)
        }
        else if(caller == "speech") {
            self.chatLineInfo.set_role("requester")
            self.postData(self.chatLine, _task: self.task)
        }
        else if(caller == "AvailableChats") {
            self.chatLineInfo.set_role("crowd")
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
    
    // MARK: Notification
    func notification(message: String) {
        var notification: UILocalNotification = UILocalNotification()
        notification.alertAction = "View"
        notification.alertBody = message
        notification.applicationIconBadgeNumber = UIApplication.sharedApplication().applicationIconBadgeNumber + 1
        notification.fireDate = NSDate(timeIntervalSinceNow: 1)
        UIApplication.sharedApplication().scheduleLocalNotification(notification)
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
    func setChatInfoFromString(data: String) -> Void {
        //parsing results from server into chatLineInfo's
        let array = split(data){$0 == "}"}.map{String($0)}
        for (var i = 0; i < array.count-1; i++) {
            //add new chatLineInfo
            //< array.count-1 because array[array.count-1] is "]"
            let lineInfo = split(array[i]){$0 == "\""}.map{String($0)}
            let cli_temp: ChatLineInfo = self.chatLineInfo.setChatLineInfo(lineInfo, chatLineInfo: ChatLineInfo())
            
            if let chatLineStart = array[i].rangeOfString("chatLine\":\"")?.startIndex {
                if let roleStart = array[i].rangeOfString("\",\"role")?.startIndex {
                    let temp_chatLine = array[i].substringWithRange(Range<String.Index>(start: advance(chatLineStart, 11), end: roleStart))
                    cli_temp.set_chatLine(temp_chatLine) //allow for quotation marks
                }
                else {
                    error("Cannot find role.")
                }
            }
            else {
                error("Cannot find start of chat line.")
            }
            
            //fill chat list
            self.cli_list.append(cli_temp)
            if(cli_temp.get_role() == "requester") {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_acceptedTime())")
                self.inputCoreData(cli_temp.get_id(), _message: cli_temp.get_chatLine(), _role: cli_temp.get_role(), _task: cli_temp.get_task(), _time: cli_temp.get_acceptedTime())
            }
            else {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_time())")
                self.inputCoreData(cli_temp.get_id(), _message: cli_temp.get_chatLine(), _role: cli_temp.get_role(), _task: cli_temp.get_task(), _time: cli_temp.get_time())
            }
        }
        self.tableView.reloadData() //update tableview
        self.scrollToBottom() //scroll to bottom of list
    }
    func setChatInfoFromData(data: [NSManagedObject]) -> Void {
        for item in data {
            var cli_temp: ChatLineInfo = ChatLineInfo()
            cli_temp.set_id(item.valueForKey("chatid")!.description)
            cli_temp.set_chatLine(item.valueForKey("message")!.description)
            cli_temp.set_role(item.valueForKey("role")!.description)
            cli_temp.set_task(item.valueForKey("task")!.description)
            if(item.valueForKey("message")!.description == "requester") {
                cli_temp.set_acceptedTime(item.valueForKey("time")!.description)
            }
            else {
                cli_temp.set_time(item.valueForKey("time")!.description)
            }
            cli_temp.set_accepted("1")
            cli_temp.set_workerId("qq9t3ktatncj66geme1vdo31u5")
            self.cli_list.append(cli_temp)
            if(cli_temp.get_role() == "requester") {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_acceptedTime())")
            }
            else {
                self.chat_list.append("\(cli_temp.get_role()) : \(cli_temp.get_chatLine()) \(cli_temp.get_time())")
            }
        }
        self.tableView.reloadData() //update tableview
        self.scrollToBottom() //scroll to bottom of list
    }
    func setChatLinesFromWeb() {
        //Pull from Chorus server and update chat page
        Alamofire.request(.GET, NSURL(string: chatURL)!, parameters: ["action" : "fetchNewChatRequester", "role": self.role, "task": self.task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": "-1"]).responseString(encoding: NSUTF8StringEncoding, completionHandler: { (_, _, result, error) in
            if(result != nil) {
                self.setChatInfoFromString(result!)
                self.scrollToBottom() //scroll to bottom of list
                if(self.can_update) {
                    self.update()
                }
            }
            
            //popup error alert
            if(error != nil) {
                self.error(error!.description)
            }
        })
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
            self.setChatInfoFromData(results)
            self.scrollToBottom()
            if(self.can_update) {
                self.update()
            }
        }
        else {
            if(error != nil) {
                self.error(error!.description)
            }
        }
    }
    //Post data to Chorus server. role = "requester"
    func postData(message: String, _task: String) -> Void {
        Alamofire.request(.POST, NSURL(string: chatURL)!, parameters: ["action" : "post", "role": "requester", "task": _task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": "\0", "chatLine": message]).responseString(encoding: NSUTF8StringEncoding, completionHandler: {(_, _, result, error) in
            if(error != nil) {
                self.error(error!.description)
            }
                //TO DO: Fix time (Current time)
            else {
                //TO DO: FIX update tableview
                self.setChatInfoFromString(result!)
                if(self.chatLineInfo.get_role() == "requester") {
                    self.inputCoreData(self.chatLineInfo.get_id(), _message: message, _role: self.role, _task: _task, _time: self.chatLineInfo.get_acceptedTime())
                }
                else {
                    self.inputCoreData(self.chatLineInfo.get_id(), _message: message, _role: self.role, _task: _task, _time: self.chatLineInfo.get_time())
                }
                if let initial = message.rangeOfString("news about ") {
                    //chat lines contains 'news about' and system returns first YahooNews link
                    self.chat_list.append(self.yahooNews(message.substringFromIndex(initial.endIndex)))
                }
                self.tableView.reloadData()
                self.scrollToBottom()
                if(self.can_update) {
                    self.update()
                }
            }
        })
    }
    //Pull from Chorus server and update chat page
    func update() {
        Alamofire.request(.GET, NSURL(string: chatURL)!, parameters: ["action" : "fetchNewChatRequester", "role": self.role, "task": self.task, "workerId": "qq9t3ktatncj66geme1vdo31u5", "lastChatId": self.chatLineInfo.get_id()]).responseString(encoding: NSUTF8StringEncoding, completionHandler: { (_, _, result, error) in
            if(result != nil) {
                if(result != "") { //new chat line
                    self.setChatInfoFromString(result!)
                    //self.scrollToBottom() //scroll to bottom of list
                    if(self.can_update == true) {
                        self.update()
                    }
                    self.notification(self.chatLineInfo.get_chatLine())
                }
            }
            
            //popup error alert
            if(error != nil) {
                self.error(error!.description)
            }
        })
    }
    
    //MARK: YahooNews (parse html)
    func yahooNews(searchTerm: String) -> String {
        var return_string: String = String()
        Alamofire.request(Method.GET, NSURL(string: "https://news.search.yahoo.com/search?p=\(searchTerm)")!).responseString(encoding: NSUTF8StringEncoding, completionHandler: {(_, _, result, error) in
            if(result != nil) {
                var split_array = result!.componentsSeparatedByString("href")
                for x in split_array {
                    println("testing: "+x+"\n")
                    if let first = x.rangeOfString("dd algo NewsArticle") {
                        println("HERE*")
                        var target_tag = x.rangeOfString("\" target=")!.startIndex
                        return_string = x.substringWithRange(Range<String.Index>(start: advance(x.startIndex, 2), end: target_tag))

                        //input into core data
                        //TO DO: Fix time (current time)
                        self.inputCoreData(self.chatLineInfo.get_id(), _message: "You might be interested in this article " + return_string, _role: "system", _task: self.task, _time: self.chatLineInfo.get_time())
                        break
                    }
                }
            }
            if(error != nil) {
                self.error(error!.description)
            }
        })
        return "system : You might be interested in this article " + return_string
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

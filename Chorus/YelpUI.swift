//
//  YelpUI.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/26/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit
import Alamofire

class YelpUI: UITableViewController {
    var searchTerm: String = String()
    var searchLocation: String = String()
    //var businesses: [Business]!
    
    let api_host: String = "api.yelp.com"
    let search_limit: String = "20"
    let search_path: String = "/v2/search"
    let business_path:String = "v2/business"
    
    let CONSUMER_KEY: String = "g5lvlciNbRFKlJv7A2qE2Q";
    let CONSUMER_SECRET: String = "3iHz_eXvupX6PUNWcd_RL6CPR-g";
    let TOKEN: String = "qJGrPQomr7PFVEaS5HEesEGLwvBlb9lX";
    let TOKEN_SECRET: String = "MNJJ19-g1oq8RwID6IwejuxRPrA";
    let SIGNATURE_METHOD: String = "HMAC-SHA1";
    let SIGNATURE:String = "u0026http%3A%2F%2Fapi.yelp.com%2Fv2%2Fsearch\\u0026limit%3D20%26location%3Dnew%2520york%26oauth_consumer_key%3Dg5lvlciNbRFKlJv7A2qE2Q%26oauth_nonce%3Dasdfghjkl%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1440780169%26oauth_token%3DqJGrPQomr7PFVEaS5HEesEGLwvBlb9lX%26term%3Dtacos";
    let NONCE:String = "asdfghjkl";

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        self.navigationController?.setToolbarHidden(true, animated: false) //hide bottom toolbar
        
        self.searchForBusinessesByLocation(self.searchTerm, location: self.searchLocation)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func timestamp(nothing:Void) -> String {
        let time:Int = Int(NSDate().timeIntervalSince1970)
        println(time.description)
        return time.description
    }
    
    // MARK: Query Yelp
    func searchForBusinessesByLocation(term: String, location: String) {
        Alamofire.request(Method.GET, NSURL(string: "http://\(self.api_host)\(self.search_path)")!, parameters: ["term":self.searchTerm, "location":self.searchLocation,"limit":self.search_limit, "oauth_consumer_key":self.CONSUMER_KEY, "oauth_token":self.TOKEN, "oauth_signature_method":self.SIGNATURE_METHOD, "oauth_signature":self.SIGNATURE, "oauth_timestamp":self.timestamp(), "oauth_nonce":self.NONCE]).responseString(encoding: NSUTF8StringEncoding, completionHandler: {(_,_,result, error) in
            if(result != nil) {
                println("result \(result)")
            }
            else {
                println("no result")
            }
            if(error != nil) {
                println("error \(error)")
            }
            else {
                println("no error")
            }
        })
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Potentially incomplete method implementation.
        // Return the number of sections.
        return 0
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete method implementation.
        // Return the number of rows in the section.
        return 0
    }

    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("business", forIndexPath: indexPath) as! UITableViewCell

        // Configure the cell...
        cell.textLabel?.text = "business"

        return cell
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using [segue destinationViewController].
        // Pass the selected object to the new view controller.
    }
    */

}

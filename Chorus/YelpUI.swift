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
    let search_limit: Int = 20
    let search_path: String = "/v2/search"
    let business_path:String = "v2/business"
    
    let CONSUMER_KEY: String = "g5lvlciNbRFKlJv7A2qE2Q";
    let CONSUMER_SECRET: String = "3iHz_eXvupX6PUNWcd_RL6CPR-g";
    let TOKEN: String = "qJGrPQomr7PFVEaS5HEesEGLwvBlb9lX";
    let TOKEN_SECRET: String = "MNJJ19-g1oq8RwID6IwejuxRPrA";

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        // MARK: Query Yelp
        self.query()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func query() {
        Alamofire.request(Method.GET, NSURL(string: "http://"+api_host+search_path)!, parameters: ["term":self.searchTerm, "location": self.searchLocation, "limit": self.search_limit, "oauth_consumer_key":self.CONSUMER_KEY, "oauth_consumer_secret":self.CONSUMER_SECRET, "oauth_token":self.TOKEN, "oauth_token_oauth_consumer_secret": self.TOKEN_SECRET]).responseString(encoding: NSUTF8StringEncoding, completionHandler: {(_, _, result, error) in
            if (result != nil) {
                println("result \(result)")
            }
            else {
                println("null result")
            }
            if (error != nil) {
                println(error!.description)
            }})
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

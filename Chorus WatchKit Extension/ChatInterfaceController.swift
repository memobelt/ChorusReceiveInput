//
//  ChatInterfaceController.swift
//  Chorus
//
//  Created by Jiachen Song on 8/25/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import Foundation
import WatchKit

class ChatInterfaceController: WKInterfaceController {
    
    @IBOutlet weak var chatRowTable: WKInterfaceTable!
    
    
    var chatLines = [["Taylor","25"], ["Katy","30"], ["Ellie","28"]]
    
    func reloadTable() {
        print(chatLines.count)
        chatRowTable.setNumberOfRows(chatLines.count, withRowType: "chatRow");
        for var i = 0; i < chatLines.count; i++ {
            let chatLine = chatLines[i]
            let cr = chatRowTable.rowControllerAtIndex(i) as! ChatRow
            cr.titleLabel.setText(chatLine[0])
            cr.detailLabel.setText(chatLine[1])
        }
        
        
    }
    
    func getDataFromParentApp(requestString: String) {
        WKInterfaceController.openParentApplication(["request": requestString],
            reply: { (replyInfo, error) -> Void in
                // TODO: process reply data
                
                if let chatData = replyInfo["chatData"] as? NSData {
                    if let chatLines = NSKeyedUnarchiver.unarchiveObjectWithData(chatData) as? [[String]] {
                        self.chatLines = chatLines
                        self.reloadTable()
                    }
                }
        })

    }
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Configure interface objects here.
        reloadTable()
        getDataFromParentApp("reviewChat")
    
    }
    
    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
        
        reloadTable()
        
    }
    
    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }
    
    
}
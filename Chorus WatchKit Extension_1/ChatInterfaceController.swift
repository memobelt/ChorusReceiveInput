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
    
    var side: String = "crowd";
    var spacing: String = "  "
    var chatLines = [["Nodata", "Nodata"]]
    
    func reloadTable() {
        var rowTypes = [String]()
        
        for var i = 0; i < chatLines.count; i++ {
            let chatLine = chatLines[i]
            if side == "crowd" && chatLine[0] == "requester" {
                rowTypes.append("chatRowLeft")
            } else if side == "crowd" && chatLine[0] == "crowd" {
                rowTypes.append("chatRowRight")
            } else {
                rowTypes.append("chatRowLeft")
            }

        }
        
        chatRowTable.setRowTypes(rowTypes)
        
        for var i = 0; i < chatLines.count; i++ {
            let chatLine = chatLines[i]
            if rowTypes[i] == "chatRowLeft" {
                let cr = chatRowTable.rowControllerAtIndex(i) as! ChatRowLeft
                cr.titleLabel.setText(spacing + chatLine[0] + spacing)
                cr.detailLabel.setText(spacing + chatLine[1] + spacing)
            }
            else if rowTypes[i] == "chatRowRight" {
                let cr = chatRowTable.rowControllerAtIndex(i) as! ChatRowRight
                cr.titleLabel.setText(spacing + chatLine[0] + spacing)
                cr.titleLabel.setText(spacing + chatLine[1] + spacing)

            }
        }
        
        chatRowTable.scrollToRowAtIndex(chatLines.count-1)
        
        
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
    
    func sendChat(message: String) {
        WKInterfaceController.openParentApplication(["request": "send", "message":message],
            reply: { (replyInfo, error) -> Void in
                // TODO: process reply data
                
        })
        
    }
    
    
    @IBAction func askChorus() {
        presentTextInputControllerWithSuggestions(["What's the weather today?", "Today's news"], allowedInputMode: .AllowEmoji)
            { (input) -> Void in
                if input != nil {
                    self.sendChat("\(input)")
                }
        }
        getDataFromParentApp("reviewChat")
        reloadTable()
    }
}
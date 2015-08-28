//
//  InterfaceController.swift
//  Chorus WatchKit Extension
//
//  Created by Jiachen Song on 8/26/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import WatchKit
import Foundation


class InterfaceController: WKInterfaceController {

    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Configure interface objects here.
    }
    
    func sendChat(message: String) {
        WKInterfaceController.openParentApplication(["request": "send", "message":message],
            reply: { (replyInfo, error) -> Void in
                // TODO: process reply data
                
        })
        
    }
    
    @IBAction func askQuestion() {
        presentTextInputControllerWithSuggestions(["What's the weather today?", "Today's news"], allowedInputMode: .AllowEmoji)
            { (input) -> Void in
                if input != nil {
                    self.sendChat("\(input)")
                }
        }
    }

    override func willActivate() {
        // This method is called when watch view controller is about to be visible to user
        super.willActivate()
    }

    override func didDeactivate() {
        // This method is called when watch view controller is no longer visible
        super.didDeactivate()
    }

}

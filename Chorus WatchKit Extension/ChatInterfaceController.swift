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
    
    var CHAT_DISPLAY_ROW = 10;
    
    func reloadTable() {
        // 1
        chatRowTable.setNumberOfRows(CHAT_DISPLAY_ROW, withRowType: "ChatRow")
        
        
    }
    
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Configure interface objects here.
        
        reloadTable();
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
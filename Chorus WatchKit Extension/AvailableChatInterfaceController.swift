//
//  AvailableChatInterfaceController.swift
//  Chorus
//
//  Created by Jiachen Song on 8/28/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import Foundation
import WatchKit

class AvailableChatInterfaceController: WKInterfaceController {
    
    @IBOutlet weak var chatRoomTable: WKInterfaceTable!
    
    
    func reloadTable() {
        chatRoomTable.setNumberOfRows(20, withRowType: "chatRoomRow")
        
        for index in 0...19 {
            if let row = chatRoomTable.rowControllerAtIndex(index) as? ChatRoomRow {
                row.chatRoom.setText("Chatroom " + String(index))
            }
        }
    }
    
    
    override func awakeWithContext(context: AnyObject?) {
        super.awakeWithContext(context)
        
        // Configure interface objects here.
        reloadTable()
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
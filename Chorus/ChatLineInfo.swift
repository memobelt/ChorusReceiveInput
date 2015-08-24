//
//  ChatLineInfo.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/14/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import Foundation

class ChatLineInfo {
    var id: String, chatLine: String, role: String, task: String, time: String, accepted: String, workerId: String, acceptedTime: String
    
    init() {
        self.id = ""
        self.chatLine = ""
        self.role = ""
        self.task = ""
        self.time = ""
        self.accepted = ""
        self.workerId = ""
        self.acceptedTime = ""
    }
    func set_id(_id: String) -> Void {
        self.id = _id
    }
    func get_id() -> String {
        return self.id
    }
    
    func set_chatLine(_chatLine: String) -> Void {
        self.chatLine = _chatLine
    }
    func get_chatLine() -> String {
        return self.chatLine
    }
    
    func set_role(_role: String) -> Void {
        self.role = _role
    }
    func get_role() -> String {
        return self.role
    }
    
    func set_task(_task: String) -> Void {
        self.task = _task
    }
    func get_task() -> String {
        return task
    }
    
    func set_time(_time: String) -> Void {
        self.time = _time
    }
    func get_time() -> String {
        return self.time
    }
    
    func set_accepted(_accepted: String) -> Void{
        accepted = _accepted
    }
    func get_accepted() -> String{
        return self.accepted
    }
    
    func set_workerId(_workerId: String) -> Void {
        workerId = _workerId
    }
    func get_workerId() -> String {
        return self.workerId
    }
    
    func set_acceptedTime(_acceptedTime: String) -> Void {
        acceptedTime = _acceptedTime
    }
    func get_acceptedTime() -> String {
        return self.acceptedTime
    }
    
    func setChatLineInfo(lineInfo: [String], chatLineInfo: ChatLineInfo) -> ChatLineInfo {
        for (var index = 1; index < lineInfo.count; index+=4)  {
            switch lineInfo[index] {
            case "id":
                chatLineInfo.set_id(lineInfo[index+2])
                break
            case "chatLine":
                chatLineInfo.set_chatLine(lineInfo[index+2])
                break
            case "role":
                chatLineInfo.set_role(lineInfo[index+2])
                break
            case "task":
                chatLineInfo.set_task(lineInfo[index+2])
                break
            case "time":
                chatLineInfo.set_time(lineInfo[index+2])
                break
            case "accepted":
                chatLineInfo.set_accepted(lineInfo[index+2])
                break
            case "workerId":
                chatLineInfo.set_workerId(lineInfo[index+2])
                break
            case "acceptedTime":
                chatLineInfo.set_acceptedTime(lineInfo[index+2])
                break
            default:
                break
            }
        }
        return chatLineInfo
    }
}
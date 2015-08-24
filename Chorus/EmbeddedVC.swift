//
//  EmbeddedVC.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/23/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit

class EmbeddedVC: UIViewController, UITextFieldDelegate {
    
    //MARK: Properties
    @IBOutlet weak var text: UITextField!
    
    //MARK: Actions
    @IBAction func send(sender: UIButton) {
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        text.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        
        //send text to Chorus Chat
        var destViewController: ChorusChat = segue.destinationViewController as! ChorusChat
        destViewController.task = "6" //temporary for login
        destViewController.chatLine = text.text
        destViewController.caller = "embedded"
    }
}
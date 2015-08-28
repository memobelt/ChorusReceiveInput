//
//  Register.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/28/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit

class Register: UIViewController, UITextFieldDelegate {
    
    //MARK: Properties
    @IBOutlet weak var chorusLogo: UIImageView!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!

    //MARK: Actions
    @IBAction func registerButton(sender: UIButton) {
        self.register()
    }
    
    // MARK: UITextFieldDelegate
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        //hide the keyboard
        textField.resignFirstResponder();
        //text field should respond to the user pressing the Return key by dismissing the keyboard
        return true;
    }
    func error(message: String) -> Void {
        var alert = UIAlertController(title: "Error", message: message, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Okay", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func register() {
        if(self.email.text.isEmpty || self.username.text.isEmpty || self.password.text.isEmpty) {
            self.error("Please fill out all information")
        }
        else {
            self.performSegueWithIdentifier("toLogin", sender: self)
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        email.delegate = self
        username.delegate = self
        password.delegate = self
        self.navigationController?.setToolbarHidden(true, animated: false) //hide bottom toolbar
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

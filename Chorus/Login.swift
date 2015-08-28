//
//  Login.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/12/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit
import Alamofire

class Login: UIViewController, UITextFieldDelegate {
    
    //MARK: Properties
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var ChorusLogo: UIImageView!
    
    var isLoggedIn:Bool = false
    
    //MARK: Actions
    @IBAction func login(sender: UIButton) {
        if(isLoggedIn == false) {
            //check login credentials
            self.checkLogin(self.username.text, _password: self.password.text)
        }
        else {
            //go to Main Activity
            self.performSegueWithIdentifier("toMainActivity", sender: self)
        }
    }
    
    //MARK: Handle login
    func error(message: String) -> Void {
        var alert = UIAlertController(title: "Error", message: message, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Okay", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    
    func checkLogin(_username: String, _password: String) {
        if((_username.isEmpty)) {
            self.error("Please enter your username")
        }
        else if((_password.isEmpty)) {
            self.error("Please enter you password")
        }
        else {
            self.isLoggedIn = true
            //go to Main Activity
            self.performSegueWithIdentifier("toMainActivity", sender: self)
        }
    }
    
    @IBAction func testButton(sender: UIButton) {
        //goes to MainActivity
    }
    
    @IBAction func register(sender: UIButton) {
        //goes to RegisterActivity
    }
    
    // MARK: UITextFieldDelegate
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        //hide the keyboard
        textField.resignFirstResponder();
        //text field should respond to the user pressing the Return key by dismissing the keyboard
        return true;
    }
    
    // MARK: other view functions
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        username.delegate = self
        password.delegate = self
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

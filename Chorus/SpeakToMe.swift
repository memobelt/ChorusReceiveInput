//
//  SpeakToMe.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/13/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit
//import SpeechKitHeader

class SpeakToMe: UIViewController, SpeechKitDelegate, SKRecognizerDelegate, UITextFieldDelegate {
    
    //MARK: Properties
    var voiceSearch: SKRecognizer?
    @IBOutlet weak var textField: UITextField!

    //MARK: Actions
    @IBAction func micButton(sender: UIButton) {
        //speech recognition
        
        self.voiceSearch = SKRecognizer(type: SKSearchRecognizerType, detection: UInt(SKLongEndOfSpeechDetection), language: "eng-USA", delegate: self)
    }
    @IBAction func sendButton(sender: UIButton) {
        //send text to ChorusChat
        //go to Chorus Chat with chat number
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        //SpeechKit setup
        //NON SSL Host: sandbox.nmdp.nuancemobility.net, Port: 443
        //SSL Host: sslsandbox.nmdp.nuancemobility.net , Port: 443
        //AppID: NMDPTRIAL_sckpeace519_gmail_com20150816112650
        /*AppKey: 0x1e 0x51 0xa4 0x95 0xa0 0x4d 0x90 0xaf 0x14 0xad 0x42 0xd6 0xba 0x28 0x03 0x83 0xb8 0x14 0x4e 0x15 0xc7 0xdb 0x2f 0xc8 0x6d 0xcc 0x97 0x5a 0x60 0xed 0x97 0x7e 0x3f 0x3c 0x13 0xdf 0x89 0xa3 0x8e 0x9e 0x51 0xd0 0x74 0x0b 0xf8 0x77 0x8f 0xb0 0x8b 0xdd 0xc5 0x53 0xb9 0xf4 0x1b 0x26 0xc0 0xb2 0x80 0x20 0x9f 0x18 0x9f 0xde
        */
        
        self.navigationController?.setToolbarHidden(true, animated: false) //hide bottom toolbar
        textField.delegate = self

        SpeechKit.setupWithID("NMDPTRIAL_sckpeace519_gmail_com20150816112650", host: "sandbox.nmdp.nuancemobility.net", port: 443, useSSL: false, delegate: self)
    }
    override func viewDidAppear(animated: Bool) {
        //put logo in navigation bar
        var nav = self.navigationController?.navigationBar
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.blueColor()
        
        //Chorus logo image title
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 100, height: 40))
        imageView.contentMode = .ScaleAspectFit
        
        let image = UIImage(named: "ChorusLogo") //load image
        imageView.image = image //set image to imageView
        
        //set title of navigation bar to image
        navigationItem.titleView = imageView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //MARK: SpeechKit (recognizer)
    func recognizerDidBeginRecording(recognizer: SKRecognizer!) {
        //the recording has started
        var alert = UIAlertController(title: "", message: "Speak into microphone", preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Done", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)    }
    func recognizerDidFinishRecording(recognizer: SKRecognizer!) {
        //the recording has stopped
        //popup alert
        var alert = UIAlertController(title: "", message: "Processing input...", preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Okay", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    func recognizer(recognizer: SKRecognizer!, didFinishWithResults results: SKRecognition!) {
        //voice recognition process has understood something
        println("recognized!")
        if(results.results.count > 0) {
            println(results.results[0].description)
            textField.text = results.results[0].description
        }
        else {
            println("no results")
        }
    }
    func recognizer(recognizer: SKRecognizer!, didFinishWithError error: NSError!, suggestion: String!) {
        //error has occured
        //popup alert
        var alert = UIAlertController(title: "Error", message: error.description, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "Okay", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
    }
    
    // MARK: UITextFieldDelegate
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        //hide the keyboard
        textField.resignFirstResponder();
        //text field should respond to the user pressing the Return key by dismissing the keyboard
        return true;
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        
        //send text to Chorus Chat
        var destViewController: ChorusChat = segue.destinationViewController as! ChorusChat
        destViewController.task = "6" //temporary for login
        destViewController.chatLine = textField.text
        destViewController.caller = "speech"
    }
}
//
//  YelpSearchUI.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/26/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit

class YelpSearchUI: UIViewController, UITextFieldDelegate {
    
    //MARK: Properties
    @IBOutlet weak var yelpLogo: UIImageView!
    @IBOutlet weak var findLabel: UILabel!
    @IBOutlet weak var nearLabel: UILabel!
    @IBOutlet weak var findText: UITextField!
    @IBOutlet weak var nearText: UITextField!
    
    //MARK: Actions
    @IBAction func submitButton(sender: UIButton) {
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.findText.delegate = self
        self.nearText.delegate = self
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
        var destViewController: YelpUI = segue.destinationViewController as! YelpUI
        destViewController.searchTerm = self.findText.text
        destViewController.searchLocation = self.nearText.text
    }
}

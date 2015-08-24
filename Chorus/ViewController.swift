//
//  ViewController.swift
//  Chorus
//
//  Created by Summer Kitahara on 8/12/15.
//  Copyright (c) 2015 Summer Kitahara. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
   
    
    //MARK: Actions
    
    @IBAction func menuButton(sender: UIBarButtonItem) {
    }
    
    
    
    //MARK: Functions
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        //setup table
        self.menuTable.registerClass(UITableViewCell.self, forCellReuseIdentifier: "menu_cell")
        menuTable.delegate = self
        menuTable.dataSource = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //MARK: Table
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 4
    }
}


//
//  ViewController.swift
//  IOSApp
//
//  Created by Nicoló Agnoletti on 30/11/22.
//

import UIKit
import Flutter
import FlutterBox

class MainViewController: UIViewController {

    @IBOutlet weak var defaultFlutterVCButton: UIButton!
    
    @IBOutlet weak var customFlutterBoxVCButton: UIButton!

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        defaultFlutterVCButton.addTarget(self, action: #selector(openDefaultFlutterVC), for: .touchUpInside)
        customFlutterBoxVCButton.addTarget(self, action: #selector(openCustomFlutterBoxVC), for: .touchUpInside)
    }

    @objc func openDefaultFlutterVC() {
        let vc = FlutterViewController()
        self.present(vc, animated: true)
    }
                                                

    @objc func openCustomFlutterBoxVC() {
        let screenID = UUID().uuidString
        
        let vc = FlutterBoxViewControllerBuilder(screenID: screenID)
            .with(options: Greeting(value: "Hello from \(screenID)!"))
            .build()
        
        vc.initializeChannels()
        
        self.present(vc, animated: true)
    }
}

extension FlutterViewController {
    public func initializeChannels() {
        let methodChannel = FlutterMethodChannel(name: "method_channel_a", binaryMessenger: self.binaryMessenger)
        methodChannel.setMethodCallHandler({ (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            switch call.method {
            case "ping":
                result("pong")
            default:
                result(FlutterMethodNotImplemented)
            }
        })
    }
}

extension Greeting: FlutterBoxOptions {
    var initialRoute: String? { return "/a" }
    
    var arguments: [String]? { return [value] }
}

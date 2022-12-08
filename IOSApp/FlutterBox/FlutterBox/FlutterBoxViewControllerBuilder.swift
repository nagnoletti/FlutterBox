//
//  FlutterBoxViewController.swift
//  FlutterBox
//
//  Created by Nicoló Agnoletti on 08/12/22.
//

import Foundation
import Flutter

public class FlutterBoxViewControllerBuilder {
    
    private let screenID: String
    private var options: FlutterBoxOptions? = nil
    
    public init(screenID: String) {
        self.screenID = screenID
    }
    
    public func with(options opts: FlutterBoxOptions) -> FlutterBoxViewControllerBuilder {
        self.options = opts
        return self
    }
    
    public func build() -> FlutterViewController {
        if let engineID = try? FlutterBox.instance.getScreenEngineID(screenID: screenID, options: options) {
            let engine = FlutterBoxEngineCache.instance.get(engineID: engineID)
            let vc = FlutterBoxViewController(engine: engine ?? FlutterEngine(), nibName: nil, bundle: nil)
            vc.screenID = self.screenID
            return vc
        } else {
            return FlutterViewController()
        }
    }
}

internal class FlutterBoxViewController: FlutterViewController {
    
    var screenID: String?
    
    override func viewDidDisappear(_ animated: Bool) {
        if let screenID = self.screenID {
            FlutterBox.instance.releaseEngineFor(screenID: screenID)
        }
    }
}

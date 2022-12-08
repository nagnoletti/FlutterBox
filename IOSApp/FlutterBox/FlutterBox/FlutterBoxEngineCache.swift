//
//  FlutterBoxEngineCache.swift
//  FlutterBox
//
//  Created by Nicoló Agnoletti on 30/11/22.
//

import Foundation
import Flutter

class FlutterBoxEngineCache: NSObject {
    
    private override init() {}
    
    public static let instance = FlutterBoxEngineCache()
    
    fileprivate var cache: [String:FlutterEngine] = [:]
    
    public func put(engineID: String, engine: FlutterEngine) {
        cache[engineID] = engine
    }
    
    public func get(engineID: String) -> FlutterEngine? {
        return cache[engineID]
    }
    
    public func remove(engineID: String) {
        let engine = cache[engineID]
        
        if engine != nil {
            // TODO find out if engine automatically dies with FlutterViewController or find the right spot to destroy engine context
            // throws bad access error when called on FlutterViewController.onViewDidDisappear
            // engine.destroyContext()
        }
        
        cache.removeValue(forKey: engineID)
    }
    
    public func clear() {
        cache.keys.forEach { self.remove(engineID: $0) }
    }
}

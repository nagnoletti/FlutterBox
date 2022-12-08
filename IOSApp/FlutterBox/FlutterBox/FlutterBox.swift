//
//  FlutterBox.swift
//  FlutterBox
//
//  Created by Nicoló Agnoletti on 30/11/22.
//

import Foundation
import Flutter

public enum FlutterBoxError: Error, CustomStringConvertible {
    case multipleInitializations
    case notInitialized
    case engineIDAlreadyUsed(String)
    case unexpected
    
    public var description: String {
        switch self {
        case .multipleInitializations:
            return "Initialize FlutterBox only once."
        case .notInitialized:
            return (
                "FlutterBox should be initialized before using it." +
                "\n Call FlutterBox.instance.initialize() before opening a Flutter screen."
            )
        case .engineIDAlreadyUsed(let eid):
            return "Engine ID \"\(eid)\" already used. Make sure to always provide distinct engine IDs."
        case .unexpected:
            return "Unexpected error."
        }
    }
}

public protocol FlutterBoxOptions {
    var initialRoute: String? { get }
    var arguments: [String]? { get }
}

public class FlutterBox: NSObject {
    
    public static let instance = FlutterBox()
    
    fileprivate static let initializerEngineID = "FlutterBox.initializerEngineID"
    fileprivate static let initializerScreenID = "FlutterBox.initializerScreenID"
    
    private var engineGroup: FlutterEngineGroup? = nil
    
    fileprivate var assignedEngineIDs: [String:String] = [:]
    
    private override init() {}
    
    public func initialize() throws {
        try checkMultipleInitializationsError {
            let eg = FlutterEngineGroup(
                name: "FlutterBoxGroup#\(NSTimeIntervalSince1970)",
                project: FlutterDartProject()
            )
            engineGroup = eg
            try eg.initialize()
        }
    }
    
    public func getScreenEngineID(
        screenID sid: String,
        options opts: FlutterBoxOptions?
    ) throws -> String {
        let engineID = try checkNotInitializedError {
            try $0.cacheAndRunNewEngineToGetID(
                screenID: sid,
                engineID: nil,
                options: opts
            )
        }
        return engineID
    }
    
    public func releaseEngineFor(screenID sid: String) {
        if let eid = assignedEngineIDs[sid] {
            assignedEngineIDs.removeValue(forKey: sid)
            if let engine = FlutterBoxEngineCache.instance.get(engineID: eid) {
                FlutterBoxEngineCache.instance.remove(engineID: eid)
                engine.destroyContext()
            }
        }
    }
    
    private func checkNotInitializedError(then: (FlutterEngineGroup) throws -> String) throws -> String {
        if let eg = engineGroup {
            return try then(eg)
        } else {
            throw FlutterBoxError.notInitialized
        }
    }
    
    private func checkMultipleInitializationsError(then: () throws -> ()) throws {
        let eg = engineGroup
        if eg != nil {
            if FlutterBoxEngineCache.instance.get(engineID: FlutterBox.initializerEngineID) != nil {
                throw FlutterBoxError.unexpected
            } else {
                throw FlutterBoxError.multipleInitializations
            }
        } else {
            try then()
        }
    }
    
    fileprivate func checkEngineIDAlreadyUsed(eid: String) throws {
        if FlutterBoxEngineCache.instance.get(engineID: eid) != nil {
            throw FlutterBoxError.engineIDAlreadyUsed(eid)
        }
    }
}

fileprivate extension FlutterEngineGroup {
    
    func cacheAndRunNewEngineToGetID(
        screenID sid: String,
        engineID eid: String?,
        options opts: FlutterBoxOptions? = nil
    ) throws -> String {
        if eid != nil {
            try FlutterBox.instance.checkEngineIDAlreadyUsed(eid: eid!)
        }
        
        let egOptions = FlutterEngineGroupOptions().apply {
            $0.entrypointArgs = opts?.arguments ?? []
            $0.initialRoute = opts?.initialRoute
        }
        
        let engine = self.makeEngine(with: egOptions)
        let engineID = eid ?? UUID().uuidString
        
        if(sid != FlutterBox.initializerScreenID) {
            FlutterBox.instance.assignedEngineIDs[sid] = engineID
        }
        
        FlutterBoxEngineCache.instance.put(engineID: engineID, engine: engine)
        return engineID
    }
    
    func initialize() throws {
        let _ = try cacheAndRunNewEngineToGetID(
            screenID: FlutterBox.initializerScreenID,
            engineID: FlutterBox.initializerEngineID
        )
    }
}

fileprivate protocol ScopeFunc {}

extension NSObject: ScopeFunc {}

fileprivate extension ScopeFunc {
    @inline(__always) func apply(block: (Self) -> ()) -> Self {
        block(self)
        return self
    }
    @inline(__always) func with<R>(block: (Self) -> R) -> R {
        return block(self)
    }
}

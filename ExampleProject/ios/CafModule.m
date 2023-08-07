//
//  CafModule.m
//  ExampleProject
//
//  Created by Murilo Fank on 02/08/23.
//

#import <Foundation/Foundation.h>

#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(CafModule, RCTEventEmitter)

RCT_EXTERN_METHOD(passiveFaceLiveness:(NSString *)mobileToken)
RCT_EXTERN_METHOD(documentDetector:(NSString *)mobileToken documentType:(NSString *)documentType)
//Comment this next method if you'll run on Iphone Simulator
RCT_EXTERN_METHOD(faceAuthenticator:(NSString *)mobileToken CPF:(NSString *)CPF)

@end

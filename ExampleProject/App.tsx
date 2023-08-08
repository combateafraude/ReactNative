/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useState, useEffect } from 'react';
import type { PropsWithChildren } from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  Button,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';

type SectionProps = PropsWithChildren<{
  title: string;
}>;

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  //Insert your generated JWT here. Check documentation here: https://docs.caf.io/sdks/access-token
  const mobileToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI1ZmMxM2U5MDE2YTgxODAwMDczNzNlMWYifQ._jdY1z1N1dfaFIq88Qk0akEgOk-taH2OxoW3oT1eLl0";
  //Insert user's CPF to run FaceAuthenticator SDK
  const CPF = "03331719005"

  const [passiveFaceLivenessResult, setPassiveFaceLivenessResult] = useState("Touch 'Start SDK' button");
  const [documentDetectorResult, setDocumentDetectorResult] = useState("Select document's type");
  const [faceAuthenticatorResult, setFaceAuthenticatorResult] = useState("Touch 'Start SDK' button");

  let module = NativeModules.CafModule;
  let moduleEmmiter = new NativeEventEmitter(module);

  //PassiveFaceLiveness
  useEffect(() => {
    moduleEmmiter.addListener(
      "PassiveFaceLiveness_Success",
      res => {
        setPassiveFaceLivenessResult(
          "Success:" + res.success +
          "\nSelfie: " + res.imageUrl
        );
      }
    )

    moduleEmmiter.addListener(
      "PassiveFaceLiveness_Error",
      res => {
        setPassiveFaceLivenessResult(
          "Error type: " + res.type +
          "\nError message: " + res.message
        );
      }
    )

    moduleEmmiter.addListener(
      "PassiveFaceLiveness_Cancel",
      res => {
        setPassiveFaceLivenessResult("User closed the SDK");
      }
    )

    //DocumentDetector
    moduleEmmiter.addListener(
      "DocumentDetector_Success",

      res => {
        setDocumentDetectorResult(
          "Front: " + res.captures[0].imageUrl +
          "\nPicture quality: " + res.captures[0].quality +
          "\n\nBack: " + res.captures[1].imageUrl +
          "\nPicture quality: " + res.captures[1].quality
        );
      }
    )

    moduleEmmiter.addListener(
      "DocumentDetector_Error",
      res => {
        setDocumentDetectorResult(
          "Error type: " + res.type +
          "\nError message: " + res.message
        );
      }
    )

    moduleEmmiter.addListener(
      "DocumentDetector_Cancel",
      res => {
        setDocumentDetectorResult("User closed the SDK");
      }
    )

    //FaceAuthenticator - Comment this last code snippet if you'll run on Simulator
    moduleEmmiter.addListener(
      "FaceAuthenticator_Success",
      res => {
        setFaceAuthenticatorResult("Is Authenticated: " + res.authenticated);
      }
    )

    moduleEmmiter.addListener(
      "FaceAuthenticator_Cancel",
      res => {
        setFaceAuthenticatorResult("User closed the SDK");
      }
    )

    moduleEmmiter.addListener(
      "FaceAuthenticator_Error",
      res => {
        setFaceAuthenticatorResult(
          "Error type: " + res.type +
          "\nError message: " + res.message
        );
      }
    )

  }, []);

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <Text style={[styles.sectionTitle, {
            color: isDarkMode ? Colors.white : Colors.black
          },]}>
            {'Passive Face Liveness'}</Text>
          <View style={styles.sectionContainer}>
            <Button
              title='Start SDK'
              onPress={() => {
                module.passiveFaceLiveness(mobileToken)
              }} />
          </View>
          <View style={styles.sectionResultContainer}>
            <Text style={[styles.sectionResultText, {
              color: isDarkMode ? Colors.white : Colors.black
            },]}>
              {'PassiveFaceLivenessResult:\n' + passiveFaceLivenessResult}</Text>
          </View>
          <Text style={[styles.sectionTitle, {
            color: isDarkMode ? Colors.white : Colors.black
          },]}>
            {'Document Detector'}</Text>
          <View style={styles.sectionContainer}>
            <Button
              title='CNH'
              onPress={() => {
                module.documentDetector(mobileToken, "CNH");
              }} />
          </View>
          <View style={styles.sectionContainer}>
            <Button
              title='RG'
              onPress={() => {
                module.documentDetector(mobileToken, "RG");
              }} />
          </View>
          <View style={styles.sectionContainer}>
            <Button
              title='RNE'
              onPress={() => {
                module.documentDetector(mobileToken, "RNE");
              }} />
          </View>
          <View style={styles.sectionContainer}>
            <Button
              title='CRLV'
              onPress={() => {
                module.documentDetector(mobileToken, "CRLV");
              }} />
          </View>
          <View style={styles.sectionContainer}>
            <Button
              title='PASSPORT'
              onPress={() => {
                module.documentDetector(mobileToken, "PASSPORT");
              }} />
          </View>
          <View style={styles.sectionContainer}>
            <Button
              title='CTPS'
              onPress={() => {
                module.documentDetector(mobileToken, "CTPS");
              }} />
          </View>
          <View style={styles.sectionResultContainer}>
            <Text style={[styles.sectionResultText, {
              color: isDarkMode ? Colors.white : Colors.black
            },]}>
              {'DocumentDetectorResult:\n' + documentDetectorResult}</Text>
          </View>

          {/* Comment the next code snippet if you'll run on Simulator 
              From here
          */}
          <Text style={[styles.sectionTitle, {
            color: isDarkMode ? Colors.white : Colors.black
          },]}>
            {'Face Authentication'}</Text>
          <View style={styles.sectionContainer}>
            <Button
              title='Start SDK'
              onPress={() => {
                module.faceAuthenticator(mobileToken, CPF)
              }} />
          </View>
          <View style={styles.sectionResultContainer}>
            <Text style={[styles.sectionResultText, {
              color: isDarkMode ? Colors.white : Colors.black
            },]}>
              {'FaceAuthenticatorResult:\n' + faceAuthenticatorResult}</Text>
          </View>
          {/* To here               */}

        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    flex: 1,
    marginHorizontal: 40,
    marginTop: 20,
    paddingHorizontal: 24,
    backgroundColor: '#42d602',
    borderRadius: 8,
  },
  sectionResultContainer: {
    flex: 1,
    alignItems: 'center',
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    marginTop: 20,
    fontSize: 18,
    fontWeight: '500',
    textAlign: 'center',
  },
  sectionResultText: {
    fontSize: 16,
    fontWeight: '400',
    lineHeight: 25,
    textAlign: 'auto',
  },
});

export default App;

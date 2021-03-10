import React, { useEffect, useState } from 'react';
import { Button, NativeEventEmitter, NativeModules, SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';
import { Colors } from 'react-native/Libraries/NewAppScreen';


const App: () => React$Node = () => {

  

  const mobileToken = "";

  const [passiveFaceLivenessResult, setPassiveFaceLivenessResult] = useState(null);
  const [documentDetectorResult, setDocumentDetectorResult] = useState(null);

  const combateAFraudeEmmiter = new NativeEventEmitter(NativeModules.CombateAFraude);


     useEffect(() => {
      combateAFraudeEmmiter.addListener(
        "PassiveFaceLiveness_Success",
        res => {
          setPassiveFaceLivenessResult("Selfie: "+res.imagePath);
        }
      )
    
      combateAFraudeEmmiter.addListener(
        "PassiveFaceLiveness_Error",
        res => {
          setPassiveFaceLivenessResult("Erro: "+res.message);
        }
      )
    
      combateAFraudeEmmiter.addListener(
        "PassiveFaceLiveness_Cancel",
        res => {
          setPassiveFaceLivenessResult("Usuário fechou");
        }
      )

      combateAFraudeEmmiter.addListener(
        "DocumentDetector_Success",
        
        res => {
          setDocumentDetectorResult("Frente: "+res.captures[0].imagePath+"\nVerso: "+res.captures[1].imagePath);
        }
      )
  
      combateAFraudeEmmiter.addListener(
        "DocumentDetector_Error",
        res => {
          setDocumentDetectorResult("Erro: "+res.message);
        }
      )
    
      combateAFraudeEmmiter.addListener(
        "DocumentDetector_Cancel",
        res => {
          setDocumentDetectorResult("Usuário fechou");
        }
      )

   }, []);

  return (
    <>
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Button style={styles.button}
                      title='PassiveFaceLiveness'
                      onPress={() => { 
                        NativeModules.CombateAFraude.passiveFaceLiveness(mobileToken)
                      }}/>
            </View>

            <View style={styles.sectionContainer}>
              <Text>{'PassiveFaceLivenessResult: ' + passiveFaceLivenessResult}</Text>
            </View>
            <View style={styles.sectionContainer}>
              <Button style={styles.button}
                      title='DocumentDetector - CNH'
                      onPress={() => {
                        NativeModules.CombateAFraude.documentDetector(mobileToken, "CNH");
                      }}/>
            </View>
            <View style={styles.sectionContainer}>
              <Button style={styles.button}
                      title='DocumentDetector - RG'
                      onPress={() => {
                        NativeModules.CombateAFraude.documentDetector(mobileToken, "RG");
                      }}/>
            </View>

            <View style={styles.sectionContainer}>
              <Text>{'DocumentDetectorResult: ' + documentDetectorResult}</Text>
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};



const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
});



export default App;
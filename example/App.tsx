import * as Settings from 'expo-settings';
import * as React from 'react';
import { Button,   Image,     View   } from 'react-native';
 import * as ImagePicker from 'expo-image-picker';

export default function App() {
  const [isImage, setImage] = React.useState('')
 
  const selectAndProcessImage = async () => { 
    const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (permissionResult.granted === false) {
      alert('Você precisa conceder permissão para acessar suas fotos!');
      return;
    }

 
    const pickerResult = await ImagePicker.launchImageLibraryAsync({
      base64: true,
      quality: 1,
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
    });
 
    const testee = await  Settings.getTheme(pickerResult.assets[0].base64)
 
    setImage(testee)

 
  };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>

<Image
        style={{
          width:'100%',
          height:100,
          marginBottom:20
        }}
        source={{
          uri: 'data:image/jpeg;base64,' + isImage,
          
        }}
      />
  
      <Button title="Processar Imagem" onPress={selectAndProcessImage} />   
    </View>
  );
}

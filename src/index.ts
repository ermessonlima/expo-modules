import ExpoSettingsModule from './ExpoSettingsModule';

export async function getTheme(theme) {
  try {
    const locationsList = await ExpoSettingsModule.getTheme(theme);
    return locationsList;
  } catch (e) {
    console.error(e);
    throw e; // ou lide com o erro de maneira adequada
  }
}

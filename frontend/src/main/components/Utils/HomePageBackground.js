import BackgroundDay from './../../../assets/HomePageBackground-day.png';
import BackgroundEvening from './../../../assets/HomePageBackground-evening.png';
import BackgroundMorning from './../../../assets/HomePageBackground-morning.png';
import BackgroundNight from './../../../assets/HomePageBackground-night.png';

//LP-adding lower resolution images JPG format
import BackgroundDay_LowRes from './../../../assets/HomePageBackground-day.jpg';
import BackgroundEvening_LowRes from './../../../assets/HomePageBackground-evening.jpg';
import BackgroundMorning_LowRes from './../../../assets/HomePageBackground-morning.jpg';
import BackgroundNight_LowRes from './../../../assets/HomePageBackground-night.jpg';

// istanbul ignore next: trivial function to ignore from coverage due to how it is structured. Should be refactored in the future so that it can be tested.
export default function getBackgroundImage(time, res) {
  if( res === 1 ) { //1 corresponds to higher res 
      if (time >= 6 && time < 9) {
        return BackgroundMorning;
      } else if (time >= 9 && time < 18) {
        return BackgroundDay;
      } else if (time >= 18 && time < 21) {
        return BackgroundEvening;
      } else {
        return BackgroundNight;
      }
    } else { 
      if (time >= 6 && time < 9) {
        return BackgroundMorning_LowRes;
      } else if (time >= 9 && time < 18) {
        return BackgroundDay_LowRes;
      } else if (time >= 18 && time < 21) {
        return BackgroundEvening_LowRes;
      } else {
        return BackgroundNight_LowRes;
      }
    }
}
import axios from 'axios';

/*
* {
* \"inBed\":1,
* \"sleepState\":3,
* \"averageRespiration\":23,
* \"averageHeartbeat\":74,
* \"turnoverNumber\":0,
* \"largeBodyMove\":0,
* \"minorBodyMove\":0,
* \"apneaEvents\":0,
* \"sleepDuration\":0,
* \"sleepQualityScore\":0
* }
*/
class Api {
  _url = process.env.REACT_APP_API_URL;

  async getSleepQualityScore() {
    ext: String = "/bedroom/sleep/quality-score";
    res: String = await axios.get(this._url+ext);
    return res;
  }

  async getTotalSleepDuration() {
    ext: String = "/bedroom/sleep/total-duration";
    res: String = await axios.get(this._url+ext);
    return res;
  }

  async getSleepTimeSeries() {
    ext: String = "/bedroom/sleep/time-series";
    res: String = await axios.get(this._url+ext);
    return res;
  }
  async getSleepMedianBPM() {
    ext: String = "/bedroom/sleep/median-bpm";
    res: String = await axios.get(this._url+ext);
    return res;
  }
  async getSleepMedianRPM() {
    ext: String = "/bedroom/sleep/median-rpm";
    res: String = await axios.get(this._url+ext);
    return res;
  }
  async getSleepStatesSeries() {
    ext: String = "/bedroom/sleep/states-series";
    res: String = await axios.get(this._url+ext);
    return res;
  }
}

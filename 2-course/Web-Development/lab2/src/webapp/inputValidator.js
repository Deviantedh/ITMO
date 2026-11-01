export default class inputValidator{
    constructor(responseCode, message) {
        this.responseCode = responseCode;
        this.message = message;
    }
    validate(xVal, yVal, rVal){
        if (!(-5 <= xVal && xVal <= 3)) {
            this.message = "The X value should be something from this array: [-5, -4, -3, -2, -1, 0, 1, 2, 3]";
            this.responseCode = 0;
        } else if (!(-3 <= yVal && yVal <= 3)){
            this.message = "The Y value should be in (-3, 3) interval";
            this.responseCode = 0;
        } else if (![1, 1.5, 2, 2.5, 3].includes(rVal)){
            this.message = "The R value should be something from this array: [1, 1.5, 2, 2.5, 3]";
            this.responseCode = 0;
        } else {
           this.responseCode = 1;
        }
    }

    validateR(rVal) {
        if (![1, 1.5, 2, 2.5, 3].includes(rVal)){
            this.message = "The R value should be something from this array: [1, 1.5, 2, 2.5, 3]";
            this.responseCode = 0;
        } else {
            this.responseCode = 1;
        }
    }

    getResponseCode(){
        return this.responseCode;
    }
    getMessage(){
        return this.message;
    }
}
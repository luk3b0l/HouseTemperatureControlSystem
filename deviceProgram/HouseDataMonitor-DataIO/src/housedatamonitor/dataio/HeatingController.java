package housedatamonitor.dataio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.concurrent.TimeUnit;

/**
 * This class provides heating controller, base on the external Pi4J library for controlling Raspberry Pi GPIO to control connected LED
 * Functionality:
 * - set LED initial status - OFF
 * - turn LED on - mimics heating on
 * - turn LED off - mimics heating off
 * @author Lukasz Bol
 */
public class HeatingController 
{
    private static GpioPinDigitalOutput heatingLED = null;
    
    public HeatingController(){}
    
    /*
    * This method sets initial heating status to LOW - heating off
    * @throws InterruptedException
    */
    public void setHeatingInitialStatus() throws InterruptedException
    {
        if(heatingLED == null)
        {
            final GpioController gpio = GpioFactory.getInstance(); //only 1 instance of GpioController for the project
            heatingLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, // PIN number
                                                                          "Heating LED", // Name of the PIN
                                                                          PinState.LOW); // PIN startup default state - LOW (LED turned off)
            System.out.println("Initial LED heating status set to OFF");
        }
    }
    
    /*
    * This method switches the LED on - mimics heating ON
    */
    public void setHeatingOn()
    {
        this.heatingLED.high();
    }
    
    /*
    * This method switches the LED off - mimics heating OFF
    */
    public void setHeatingOff()
    {
        this.heatingLED.low();
    }

    /*
    * This method returns GPIOPinDigitalOutput representation of LED
    @return heatingLED - returns GPIOPinDigitalOutput type of LED to further get LED status by using inner method getState()
    */
    public GpioPinDigitalOutput getHeatingLED() {
        return heatingLED;
    }    
}

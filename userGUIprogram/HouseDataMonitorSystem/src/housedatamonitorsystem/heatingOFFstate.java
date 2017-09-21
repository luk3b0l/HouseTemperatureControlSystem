package housedatamonitorsystem;

/**
 *
 * @author Lukasz Bol
 */
public class heatingOFFstate implements State
{
    public heatingOFFstate(heatingLED hLED){}

    @Override
    public void heatingTurningON() 
    {
        System.out.println("Turning heating ON");
    }

    @Override
    public void heatingTurningOFF() 
    {
        System.out.println("Turning heating OFF");
    }  
}

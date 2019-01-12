/**
* Clamps a value between a minimum and maximum, inclusive.
* 
* @param val the value to clamp
* @param min the minimum value
* @param max the maximum value
* @return {@code val}, if {@code val} is between [{@code min}, {@code max}]
* 		   {@code min}, if {@code val} is <= {@code min}
* 		   {@code min}, if {@code val} is >= {@code min}
*/
fun clamp(value: Double, min: Double, max: Double) : Double {
    return when{
        value <= min -> min
        value >= max -> max
        else -> value
    }
}
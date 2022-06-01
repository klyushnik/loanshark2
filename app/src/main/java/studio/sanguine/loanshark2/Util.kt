package studio.sanguine.loanshark2

import java.text.SimpleDateFormat
import java.util.*

class Util {

    companion object{
        fun withInterest(initialAmount: Double,
                            interestRate: Double,
                            interestType: String,
                            startDate: String) : Double {

            var interest = 0.0
            var unitsPassed = 0
            val seconds = 86400000

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val initDate = simpleDateFormat.parse(startDate)
            val todaysDate = Date()

            if (initDate != null) {
                unitsPassed = ((initDate.time - todaysDate.time) / seconds.toDouble()).toInt()
            }

            when(interestType){
                "d" -> {} //daily, leave as is
                "w" -> {unitsPassed /= 7} //weekly, divide by 7
                "bw" -> {unitsPassed /= 14} //bi-weekly
                "m" -> {unitsPassed /= 30} //monthly
                "y" -> {unitsPassed /= 365} //yearly
                else -> {unitsPassed = 0} //this includes "n", which is no interest, as well as any other value
            }

            if (unitsPassed > 0){
                for(i in 0 until unitsPassed){
                    interest += initialAmount * interestRate / 100
                }
            }

            return initialAmount + interest
        }

        fun compareDates(currentDate: String, dueDate: String) : Long {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val _currentDate = simpleDateFormat.parse(currentDate)
            val _dueDate = simpleDateFormat.parse(dueDate)
            return _dueDate.time - _currentDate.time
        }
    }

}
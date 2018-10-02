package com.scottlogic.deg.classifier.simple_classifier

import com.scottlogic.deg.classifier.{CurrencyType, SemanticType}

object CurrencyClassifier extends Classifier {
  private val currencyCodes : List[String] = List[String](
        "AFA","ALL","DZD","USD","ESP","FRF","ADP","AOA","XCD","XCD","ARS","AMD","AWG","AUD","ATS",
        "AZM","BSD","BHD","BDT","BBD","BYB","RYR","BEF","BZD","XOF","BMD","INR","BTN","BOB","BOV",
        "BAM","BWP","NOK","BRL","USD","BND","BGL","BGN","XOF","BIF","KHR","XAF","CAD","CVE","KYD",
        "XAF","XAF","CLP","CLF","CNY","HKD","MOP","AUD","AUD","COP","KMF","XAF","CDF","NZD","CRC",
        "XOF","HRK","CUP","CYP","CZK","DKK","DJF","XCD","DOP","TPE","IDE","ECS","ECV","EGP","SVC",
        "XAF","ERN","EEK","ETB","DKK","XEU","EUR","FKP","FJD","FIM","FRF","FRF","XPF","XPF","XAF",
        "GMD","GEL","DEM","GHC","GIP","GRD","DKK","XCD","FRF","USD","GTQ","GNF","GWP","XOF","GYD",
        "HTG","USD","AUD","ITL","HNL","HUF","ISK","INR","IDR","XDR","IRR","IQD","IEP","ILS","ITL",
        "JMD","JPY","JOD","KZT","KES","AUD","KPW","KRW","KWD","KGS","LAK","LVL","LBP","ZAR","LSL",
        "LRD","LYD","CHF","LTL","LUF","MKD","MGF","MWK","MYR","MVR","XOF","MTL","USD","FRF","MRO",
        "MUR","MXN","MXV","USD","MDL","FRF","MNT","XCD","MAD","MZM","MMK","ZAR","NAD","AUD","NPR",
        "ANG","NLG","XPF","NZD","NIO","XOF","NGN","NZD","AUD","USD","NOK","OMR","PKR","USD","PAB",
        "USD","PGK","PYG","PEN","PHP","NZD","PLN","PTE","USD","QAR","FRF","ROL","RUR","RUB","RWF",
        "XCD","FRF","XCD","XCD","SHP","WST","ITL","STD","SAR","XOF","SCR","SLL","SGD","SKK","SIT",
        "SBD","SOS","ZAR","ESP","LKR","SDP","SRG","NOK","SZL","SEK","CHF","SYP","TWD","TJR","TZS",
        "THB","XOF","NZD","TOP","TTD","TND","TRL","TMM","USD","AUD","UGX","UAH","AED","GBP","USD",
        "USS","USN","USD","UYU","UZS","VUV","VEB","VND","USD","USD","XPF","MAD","YER","YUN","ZRN",
        "ZMK","ZWD"
      )
  override val semanticType: SemanticType = CurrencyType
  override def matches(input: String): Boolean = currencyCodes.contains(input.toUpperCase)
}

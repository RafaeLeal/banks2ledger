(ns banks2ledger.aux.bigdec
  (:import (java.text DecimalFormatSymbols DecimalFormat)))

(defn parse [s decimal-separator grouping-separator]
  (let [pattern "#,#.#" ;; see java DecimalFormat
        dfs (doto (DecimalFormatSymbols.)
              (.setDecimalSeparator  decimal-separator)
              (.setGroupingSeparator grouping-separator))
        df (DecimalFormat. pattern dfs)]
    (bigdec (.parse df s))))

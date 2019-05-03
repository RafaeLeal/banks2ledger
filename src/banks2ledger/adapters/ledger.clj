(ns banks2ledger.adapters.ledger
  (:require clojure.string
            [banks2ledger.aux.bigdec :as aux.bigdec]))

(defn remove-comments [s]
  (clojure.string/replace s #"(#|;|\||\*|%).*" ""))

(defn transaction? [s]
  (re-find  #"^\s*\d{4}/\d{2}/\d{2}\s+" s))

(defn string->entry [s {:keys [decimal-separator grouping-separator] :as _options}]
  (let [[acc tx-value] (clojure.string/split s #"\s+" 2)
        [currency value] (clojure.string/split tx-value #"(?=-?\d)" 2)]
    #:entry {:account  acc
             :value    (aux.bigdec/parse value decimal-separator grouping-separator)
             :currency (clojure.string/trim currency)}))

(defn transaction-str->transaction [tx-str options]
  (let [[first-line & rest] (->> (clojure.string/split tx-str #"\r?\n")
                                 (filter (complement clojure.string/blank?))
                                 (map clojure.string/trim))
        [date description] (clojure.string/split first-line #"\s" 2)]
    #:transaction {:date        date
                   :description description
                   :entries     (map #(string->entry % options) rest)}))

(defn string->seq-string
  "Transform a ledger file string into seq of strings representing transactions"
  [s]
  (clojure.string/split s #"(\r?\n){2}"))

(defn string->transactions [s]
  (->> (remove-comments s)
       string->seq-string
       (filter transaction?)
       (map #(transaction-str->transaction % {:decimal-separator \.
                                              :grouping-separator \,}))))

(comment
  (string->entry "Liabilities:NuCreditCard                       R$ -11.00")
  (decimal-separator-parser "-7.30")
  (transaction?
    "2019/04/22 99Taxi
  Expenses:Transport:UberLike                    R$ 21.60
  Liabilities:NuCreditCard                       R$ -21.60"))

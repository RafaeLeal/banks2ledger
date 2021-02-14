(ns banks2ledger.adapters.ledger-test
  (:require [clojure.test :refer :all]
            [banks2ledger.adapters.ledger :as adapters.ledger]))

(deftest transaction?-test
  (is (adapters.ledger/transaction?
        "2019/04/22 99Taxi
      Expenses:Transport:UberLike                    R$ 21.60
      Liabilities:NuCreditCard                       R$ -21.60")))

(deftest parse-entry
  (is (= (adapters.ledger/string->entry "Expenses:Transport           R$ 21.60"
                                        {:decimal-separator \.
                                         :grouping-separator \,})
         #:entry{:account "Expenses:Transport", :value 21.6M, :currency "R$"})))

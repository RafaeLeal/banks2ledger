(ns banks2ledger.new-core
  (:require [clojure.tools.cli :as tools.cli]
            [banks2ledger.adapters.ledger :as adapters.ledger]))

(def cli-options
  [["-l" "--ledger-file FILENAME" "Ledger file to get accounts and probabilities"
    :default "ledger.dat"]
   ["-f" "--csv-file FILENAME" "Input transactions in CSV format"
    :default "transactions.csv"]
   ["-a" "--account ACCOUNT" "Originating account of transactions"
    :default "Assets:Checking"]
   ["-e" "--csv-file-encoding ENCODING" "Encoding of the CSV file"
    :default "UTF-8"]
   ["-g" "--amount-grouping-separator CHAR" "Decimal group (thousands) separator character"
    :parse-fn #(first %)
    :default \,]
   ["-s" "--amount-decimal-separator CHAR" "Decimal sign character"
    :parse-fn #(first %)
    :default \.]
   ["-F" "--csv-field-separator CHAR" "CSV field separator"
    :default ","]
   [nil "--debug" "Include debug information in the generated output"
    :default false]
   ["-d" "--date-col NUMBER" "Date column index (zero-based)"
    :parse-fn #(Integer/parseInt %)
    :default 0]
   ["-h" "--csv-skip-header-lines NUMBER" "CSV header lines to skip"
    :parse-fn #(Integer/parseInt %)
    :default 0]
   ["-D" "--date-format DATE-FORMAT" "Format of date field in CSV file"
    :default "yyyy-MM-dd"]
   ["-c" "--currency" "Currency"
    :default "SEK"]
   ["-t" "--descr-col TEXT-DESCRIPTOR" "Text (descriptor) column index specs (zero-based)"
    :default "%3"]
   ["-r" "--ref-col NUMBER" "Payment reference column index (zero-based)"
    :parse-fn #(Integer/parseInt %)
    :default -1]
   ["-z" "--csv-skip-trailer-lines NUMBER" "CSV trailer lines to skip"
    :parse-fn #(Integer/parseInt %)
    :default 0]
   [nil "--hooks-file FILENAME" "Hooks file defining customized output entries"]
   ["-m" "--amount-col NUMBER" "Amount column index (zero-based)"
    :parse-fn #(Integer/parseInt %)
    :default 2]])

(defn main- [& args]
  (let [{:keys [options summary errors] :as r} (tools.cli/parse-opts args cli-options)]
    r))

(comment
  (main- "-l" "loyal.ledger")
  (main- "-l" "loyal.ledger" "-h" "42")
  (tools.cli/parse-opts ["-l" "loyal.ledger" "-sa" "42"] cli-options)
  (tools.cli/parse-opts ["-l" "loyal.ledger"] cli-options)
  (slurp "/Users/rafaeleal/Desktop/nubank-2019-03.csv")
  (def l (slurp "/Users/rafaeleal/Code/Others/accounting/loyal.ledger"))

  (adapters.ledger/string->transactions l)
  (->> (adapters.ledger/string->seq-string l)
       (filter adapters.ledger/transaction?)
       (map adapters.ledger/transaction-str->transaction)))

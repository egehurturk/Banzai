TOKENS = {
    "regulation" : "regulation_type",
    "action"     : "action_type",
    "ip"         : "identifier",
    "server"     : 'identifier',
    "allow"      : "operation",
    "deny"       : "operation",
    "from"       : "descriptor",
    "to"         : "descriptor",
    "when"       : "operator",
    "connects"   : "action_type",
    "do"         : "descriptor",
    "requests"   : "action_type",
    "print"      : "action_type",
    "prints"     : "action_type",
    "printf"     : "action_type",
    "printsf"    : "action_type",
}




FLOW = [
    ' printf "{$var.name} is name" ',
    ' printsf to 192.168.1.109 "hello there" '
    ' when ip ipAddr from 192.168.*.* connects do greet($ipAddr) '
    ' when ip var from *.*.*.* connects to prints to $var.host "{$var.host} Welcome!" '
    ' record House { address, age } ',
    ' House h -> ("Avenue", 34) ',
    ' printf {$h.address} ',



]






SAMPLE_PROGRAM = """





record Transaction {
    name
    money
}



regulation ip_config {
    allow ip from 192.*.*.*
    deny  ip from 192.168.8.1
}


regulation hooks {

    when ip local from 192.*.*.* connects do greet_ip($local)

    when ip openNet from 34.*.*.* connects do greet_public($openNet)

    when ip reserved from 12.*.*.* requests do print_tra($reserved)

}


action print_tra(reserved) {
    Transaction t -> ("Deposit 40$", "342")
    printsf to $reserved.host "Your transaction is: {$t.name}, and money is {$t.money}"
}







"""


















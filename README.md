# Coffeechain

[<img src="https://github.com/mir-one/3D-Assets/blob/master/CoffeeCoin.gif">](https://github.com/mir-one/3D-Assets/)

# /root

* sudo apt-get update
* sudo apt-get upgrade
* sudo apt-get install software-properties-common
* sudo add-apt-repository -y ppa:webupd8team/java
* sudo apt-get update
* sudo apt-get -y install oracle-java8-installer
* echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
* sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
* sudo apt-get install apt-transport-https
* sudo apt-get update
* sudo apt-get install sbt
* sudo apt-get install nano
* git clone https://github.com/coffeechain-io/node.git
* cd node
* sbt packageAll
* sbt "test:runMain tools.GenesisBlockGenerator src/test/resources/genesis.cof.conf"

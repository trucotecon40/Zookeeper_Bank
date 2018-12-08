# Zookeeper_Bank
## Pasos para ejecutar
### Crear el ensemble zookeeper (Standalone, sólo 1 servidor zookeeper)

Especificar el directorio de trabajo de zookeeper en el fichero de configuración .cfg con el valor dataDir
```
mkdir -p [dataDir]
echo 1 > [dataDir]/myid
```

En mi caso tengo dataDir=/tmp/zk por lo que tengo que ejecutar:
```
mkdir -p /tmp/zk
echo 1 > /tmp/zk/myid
```
Configurar el path para los scripts de línea de comandos de zookeeper:
```
export PATH=$PATH:[path_to_zookeeper]/bin
```
Arrancar el server de zookeeper:
```
zkServer.sh start [path_to_file_configuration.cfg]
```
Ejecutar un cliente del servidor:
```
zkCli.sh –server localhost:2181
```
Cuando hayamos terminado y querramos parar el server de zookeeeper hay que ejecutar: 
```
zkServer.sh stop [path_to_file_configuration.cfg]
```
### Ejecutar la aplicación
Configurar el classpath para las librerías java de zookeeper:
```
export CLASSPATH=$CLASSPATH:[path_to_zookeeper]/zookeeper-3.4.13.jar
export CLASSPATH=$CLASSPATH:[path_to_zookeeper]/lib/*
```
Exportar desde eclipse el proyecto como .jar y añadir dicho JAR al classpath
```
export CLASSPATH=$CLASSPATH:[path_to .jar folder]/Zookeeper_Bank.jar
```
Ejecutar el método main de la aplicación:
```
java es.upm.dit.cnvr.crudzk.MainBank
```
Abrir tantos terminales como bancos se deseen crear y en el primero de ellos ejecutar ambos apartados y en los terminales restantes simplemente será necesario ejecutar este último apartado (Ejecutar la aplicación).

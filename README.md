# NIDRA-COIN
criptomoneda propia, aun en proceso, creada en java, a partir de el conocimiento de la red
Esta conectado aun servidor de la nube de amazon por defecto, si quereis probarlo, podeis descargar los .jar ya compilados en la carpeta dist y se conectara a esta
Esta bajo licencia libre, si lo vais a usar, modificar, etc espero que no olvideis atribuirmelo, en todo caso espero que podais aprender, y yo tambien.

COMO CREAR UNA WALLET PROPIA:
Teneis que conseguir un par de claves de encriptacion en esta web (515bit): https://www.devglan.com/online-tools/rsa-encryption-decryption
y en el archivo info, poner en la primera linea la publica y en la segunda la privada, por seguridad nunca reveleis vuestra clave privada.

COMO UNIR EL NODO A LA RED:
Si quieres tener un nodo conectado a la red, simplemente busca tu ip publica en google, abre los puertos 9001 y 9002 en windows y en tu router, en el archivo Nodes.txt tienes que poner esa ip publica en la primera linea, las demas corresponden a otros nodos, actualmente solo uno de un servidor en amazon.

COMO MINAR:
Copia la direccion publica de tu wallet al iniciar el minero, y has click en Connect, este se conectara al nodo que este en su casilla, en este caso al servidor de amazon. El minado es lento debido a la red, la seguridad, y para evitar la desvalorizacion rapida de la moneda, aun asi deberia ser facil conseguir unas cuantas miles de monedas en solo unas horas. La red esta en pruebas asi que son posibles algunos problemas.

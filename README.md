# Messager
 
<b>Messager</b> is a client/server messaging application written in <b>Java</b>.

</br>

The server program is a command line application, and can be run using the following command, from the project root 'Messager' directory:

<code>java -cp target/classes;lib/* com/danzabarr/messager/server/Server boolean:localhost int:port string:db_hostname int:db_port string:db_database string:db_username string:db_password</code>

The server also requires a <b>MySQL database</b> be accessible from the supplied credentials, and expects a user table with the following fields:

<table>
  <tr>
    <th>id</th>
    <th>username</th>
    <th>password</th>
  </tr>
</table>

To run the server using a public/external IP address, you will need to set up port forwarding.


Messager is built using <b>Maven</b> and an executable jar file for the client program is available here: <a href="https://github.com/danzabarr/Messager/tree/main/out/artifacts/Messager_jar">out/artifacts/Messager_jar/Messager.jar</a>

The client GUI was built using <b>JavaFX</b> and <b>SceneBuilder</b>.

The client can also be run as a command line application. 
<ul>
<li>To run, download the contents of the Messager project, specifically the 'out' and 'lib' directories</li>
<li>Set the working directory to the Messager project directory.</li>
<li>Execute the following command with the host and port arguments:</br>
<code>java -cp out/production/Messager;lib/* com/danzabarr/messager/client/Client host port</code></li>
</ul>


Messages are sent and received using <b>RSA and AES</b> encryption. Passwords are hashed using the <b>BCrypt</b> algorithm.

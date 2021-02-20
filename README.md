# chat-multicast-java-WillMenegaldi
chat-multicast-java-WillMenegaldi created by GitHub Classroom

O projeto é constituído de 3 classes: Client, Server e Room.

Para executar o projeto, deve-se:

- Rodar a classe Server (a porta está configurada na classe Server). Porta: 1234
- Rodar a classe Client para cada usuário dentro do chat.
  Para iniciar, não é preciso enviar parametros para nenhuma das classes.
  
Ao rodar o Server e o Client, o Client deverá enviar um comando para o servidor executar determinada ação.

Os comandos são:

list_rooms
create_room
enter_room
list_members
leave_room

A especificação dos comandos é a seguinte:

list_rooms: 
  - Este comando não necessita de parâmetros 
  - Para executá-lo basta digitar: list_rooms
  - Sua função é retornar as salas já criadas.
  
create_room:
  - Este comando necessita de 3 parâmetros;
  - Exemplo de execução: create_room <nome_da_sala> <ip_multicast> <porta_multicast>
      O ip multicast deve ser válido ( 224.0.0.1 - 239.255.255.255 )
      A porta deve ser válida ( ex: 3452 )
  - Sua função é criar uma nova sala Multicast.
  
enter_room
  - Este comando necessita de 2 parâmetros
  - Exemplo de execução: enter_room <nome_da_sala> <seu_nome>
      Ao executar este comando, o usuário será adicionado à lista de membros daquela sala e já entrará no chat multicast, 
      pronto para enviar e receber mensagens referentes àquela sala.
  - Sua função é adicionar o usuário ao chat multicast
  
list_members
  - Este comando necessita de 1 parâmetro
  - Exemplo de execução: list_members <nome_da_sala>
  - Sua função é listar os membros de uma determinada sala

leave_room
  - Este comando necessita de 1 parâmetro
  - Exemplo de execução: leave_room <nome_da_sala>
  - Sua função é remover o usuário da lista de usuários daquela sala
  
Além disso, enquanto o usuário está no fluxo do chat multicast, para sair da conversa, o comando a ser executado para sair é "leave_chat"
Para isso, basta enviar como mensagem: leave_chat

Quem envia a mensagem não a recebe de volta, há uma verificação para isso;
Há validação de comandos inválidos;
Ao sair do chat multicast o usuário nao e removido da lista de usuarios da sala, para isso o comando leave_room deve ser executado;
Ao enviar um comando sem os parametros necessários, as mensagens de erro exibem o formato correto do comando a ser enviado. 
  Ex: "Two arguments must be sent for this command. enter_room <room_name> <user_name>";
Além disso, em comandos que necessitam do nome da sala, se não existir a sala enviada por parâmetro, também há mensagem de erro informando ao usuário;
Todos os comandos retornam alguma mensagem do servidor para o usuário;

# trab_SDI
Trabalho sobre multicast

`make`

`make all`

-> Cliente entra em contato com servidor (socket)

-> Sevidor avisa em multicast que o cliente entrou

-> Toda mensagem que o cliente enviar para o servidor, este manda em multicast

-> Todos os outros clientes recebem a mensagem

# TODO:
[ ] MulticastReceiver conseguir escrever mensagem e mandar para o MultiThreadServer

[X] Cada mensagem nova o MultiThreadServer manda essa mensagem pro MulticastServer

[X] Apenas usuários que sabem a senha mestra e o login mestre podem entrar

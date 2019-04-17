# MySQL Debugging

To connect to your database:

1. Run an Ubuntu pod that you can use as a client:
    ```
    kubectl run -i --tty ubuntu --image=ubuntu:16.04 --restart=Never -- bash -il
    ```

2. Install the mysql client:
    ```
    $ apt-get update && apt-get install mysql-client -y
    ```

3. Connect using the mysql cli, then provide your password:
    ```
    $ mysql -h wintering-rodent-mysql -p
    ```
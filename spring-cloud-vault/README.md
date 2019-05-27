# Spring Cloud Vault
This is a demo for Spring Cloud Vault, an integration of [Hashicorp Vault](https://www.vaultproject.io) for Spring.

## Key Reference
See Spring's official documentation: https://spring.io/guides/gs/vault-config

## Getting Started
### Local (MacOS)
1. Install Hashicorp Vault using [Homebrew](https://brew.sh/).
	```
	brew install vault
	```
2. Upon installation, Vault server is not running automatically. Execute the following command to run it. 
	```
	vault server --dev --dev-root-token-id="00000000-0000-0000-0000-000000000000"
	```
3. Export Vault address to your terminal to override the default to HTTP (no SSL).
	```
	export VAULT_ADDR="http://127.0.0.1:8200"
	```
4. Write values to Vault.
	```
	vault kv put secret/vault-client myvalue=from-vault
	```

### Try Spring Cloud Vault
1. Run the Spring Boot application
	```
	mvn spring-boot:run
	```
2. Go to http://localhost:8080/show-value and you shall see it returns `from-vault`.
	```
	$ http :8080/show-value
	HTTP/1.1 200 OK
	Content-Length: 10
	Content-Type: text/plain;charset=UTF-8
	
	from-vault
	```

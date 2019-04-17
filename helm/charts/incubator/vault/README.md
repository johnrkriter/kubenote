# incubator/vault
Linked to: [`incubator/vault` from Official Helm Repo](https://github.com/helm/charts/tree/master/incubator/vault)

## Changes against the official `incubator/vault`
* Set `vault.dev: false` by default
* Enabled Consul as backend by default
* Set Consul gossip secret name as "consul-gossip-key" by default.

## Deploying to Minikube
*Reference: [Guide from Hashicorp](https://github.com/hashicorp/vault-guides/tree/master/operations/provision-vault/kubernetes/minikube)*

## Prerequisite
Given Consul is:
* Used as backend
* Running in a different namespace.
* having gossip traffic encrypted (`Gossip.Encrypt: true` for `stable/consul`)

Before deploying, the Consul gossip secret needs to be copied to the namespace where Vault will be running at.

Assuming Consul is in `consul` namespace and Vault will be in `vault` namespace, run the following command to copy the Consul gossip secret over.
```
kubectl get secret consul-gossip-key --namespace=consul --export -o yaml | kubectl apply --namespace=vault -f -
```

### `vault operator init`
> Initialization is the process configuring the Vault. This only happens once when the server is started against a new backend that has never been used with Vault before. When running in HA mode, this happens once per cluster, not per server.

This is to [initialize](https://learn.hashicorp.com/vault/getting-started/deploy#initializing-the-vault) Vault configuration process.

_Note: The script below set the key threshold as 1 for easy demonstration purposes. It is recommended to set the key threshold as 5 for production use._
```
$ kubectl exec -it <POD_NAME> -c vault -- /bin/sh -c 'VAULT_ADDR=http://localhost:8200 vault operator init -key-shares=1 -key-threshold=1'

Unseal Key 1: oVo+GJ+mnPb/bbQvb7mkdLroJjp/v4PgE54bZxERKPw=
Initial Root Token: 02aa27af-cd84-5a15-08a3-0bcd6492e768

Vault initialized with 1 keys and a key threshold of 1. Please
securely distribute the above keys. When the vault is re-sealed,
restarted, or stopped, you must provide at least 1 of these keys
to unseal it again.

Vault does not store the master key. Without at least 1 keys,
your vault will remain permanently sealed.
```

### `vault operator unseal`
> Every initialized Vault server starts in the sealed state. From the configuration, Vault can access the physical storage, but it can't read any of it because it doesn't know how to decrypt it. The process of teaching Vault how to decrypt the data is known as unsealing the Vault.

This step is to [unseal](https://learn.hashicorp.com/vault/getting-started/deploy#seal-unseal) the Vault to make it ready for use.
```
$  for v in `kubectl get pods | grep vault | cut -f1 -d' '`; do kubectl exec -ti $v -c vault -- /bin/sh -c 'VAULT_ADDR=http://localhost:8200 vault operator unseal oVo+GJ+mnPb/bbQvb7mkdLroJjp/v4PgE54bZxERKPw='; done

Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
```

### `vault status`
This step retrieves the Vault status to verify the Vault is unsealed and ready for use.
```
$ for v in `kubectl get pods | grep vault | cut -f1 -d' '`; do kubectl exec -ti $v -c vault -- /bin/sh -c 'VAULT_ADDR=http://localhost:8200 vault status'; done

Type: shamir
Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
Version: 0.9.0.1+ent
Cluster Name: vault-cluster-6781fe0f
Cluster ID: abbfe64b-2bfc-884b-720d-aef198ebaebd

High-Availability Enabled: true
        Mode: active
        Leader Cluster Address: https://172.17.0.9:8201
Type: shamir
Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
Version: 0.9.0.1+ent
Cluster Name: vault-cluster-6781fe0f
Cluster ID: abbfe64b-2bfc-884b-720d-aef198ebaebd

High-Availability Enabled: true
        Mode: standby
        Leader Cluster Address: https://172.17.0.9:8201
Type: shamir
Sealed: false
Key Shares: 1
Key Threshold: 1
Unseal Progress: 0
Unseal Nonce:
Version: 0.9.0.1+ent
Cluster Name: vault-cluster-6781fe0f
Cluster ID: abbfe64b-2bfc-884b-720d-aef198ebaebd

High-Availability Enabled: true
        Mode: standby
        Leader Cluster Address: https://172.17.0.9:8201
```

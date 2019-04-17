# <CHARTNAME>

## Configuration options

| Parameter      						| Description                                                	 							| Default 														|
| ------------------------------------- | ----------------------------------------------------------------------------------------- | :-----------------------------------------------------------: |
| `image.repository` 		    		| Docker Repository for to push the helm to         			 							| `my-chart-repo.com/<CHARTNAME>` 								|
| `image.tag`  				        	| The versioning of the current helm chart/project				 							| `0.0.1-SNAPSHOT`     											|
| `image.pullPolicy`  					| Image pull policy												 							| `Always`     											     	|
| `service.type`	  					| Type determines how the Service is exposed					 							| `ClusterIP`     											    |
| `service.externalPort`  				| Image pull policy												 							| `8080`   	  											     	|
| `service.internalPort`  				| Image pull policy												 							| `8080`	     											    |
| `livenessProbe.httpGet.path`  		| HTTPGet specifies the http request to perform liveness check. 							| `/actuator/health`											|
| `readinessProbe.httpGet.path`  		| HTTPGet specifies the http request to perform readiness check.							| `/actuator/health`											|
| `env` 	        		    		| Environment variables				                         	 							| `- SPRING_PROFILES_ACTIVE: kubernetes`      			   		|

### Complete configuration list for Helm chart:
* https://helm.sh/docs/developing_charts/
* https://kubernetes.io/docs/concepts/
* https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.13/

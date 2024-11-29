Prequisits 

Docker or e.g. colima is needed as the Keycloak server
runs in Testcontainers.

On Mac you will likely need to make sure to have a softlink to the docker socket, e.g.

```
sudo ln -s /Users/bernhard.ocklin/.colima/docker.sock /var/run/docker.sock 
```
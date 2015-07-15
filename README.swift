
For extracting configurations use Config class. 
Examples:
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Config

For extracting dispersion  use Dispersion class. 
curl -d '{"msg":"all"}' http://localhost:8080/external?class=Dispersion

To check the status of the each server use the Init class
Examples:
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Init

To get the Recon status of each server use the Recon class
Examples:
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Recon

To find the status of each ring use the Ring class
Examples:
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Ring
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Ring
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Ring


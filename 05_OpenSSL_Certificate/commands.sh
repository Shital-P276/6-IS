//Commands
openssl genrsa -out b77.key 2048
openssl rsa -in b77.key -pubout -out b77_public.key
openssl req -new -key b77.key -out b77.csr
openssl req -text -in b77.csr -noout -verify
openssl x509 -in b77.csr -out b77.crt -req -signkey b77.key -days 365


//Terminal
ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ openssl genrsa -out b77.key 2048

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ dir
b77.key

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ openssl rsa -in b77.key -pubout -out b77_public.key
writing RSA key

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ dir
b77.key  b77_public.key

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ openssl req -new -key b77.key -out b77.csr
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:IN
State or Province Name (full name) [Some-State]:MH
Locality Name (eg, city) []:KOP
Organization Name (eg, company) [Internet Widgits Pty Ltd]:KIT
Organizational Unit Name (eg, section) []:CSE
Common Name (e.g. server FQDN or YOUR name) []:b77
Email Address []:b77@gmail.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ dir
b77.csr  b77.key  b77_public.key

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ openssl req -text -in b77.csr -noout -verify
Certificate request self-signature verify OK
Certificate Request:
    Data:
        Version: 1 (0x0)
        Subject: C = IN, ST = MH, L = KOP, O = KIT, OU = CSE, CN = b77, emailAddress = b77@gmail.com
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (2048 bit)
                Modulus:
                    00:ba:96:90:94:e5:81:79:e1:bc:b7:5a:f6:6e:a7:
                    1a:28:2f:5a:5b:db:d6:57:2e:99:61:e5:c8:13:8a:
                    74:fd:71:ca:7e:29:4d:0e:74:6f:e1:97:02:f5:8d:
                    89:b5:ef:80:82:9a:2f:46:92:e3:14:34:f1:51:da:
                    f1:38:39:c4:26:44:6e:3c:6b:90:cf:4a:94:8e:65:
                    e1:d9:0c:b0:0c:04:b2:13:34:a3:0d:49:1a:01:45:
                    a9:fe:b5:51:46:91:6c:2e:a9:19:5f:f5:97:9a:f3:
                    30:35:dd:a4:68:7b:4d:55:30:f4:04:c7:c2:3a:94:
                    20:f1:75:47:2e:46:3d:c2:3f:da:bc:34:11:8a:6e:
                    05:9e:c4:0e:d1:9c:9b:8b:40:58:a2:cb:98:1e:3f:
                    e9:d3:86:b1:aa:51:e0:d7:5f:39:19:5a:74:61:cf:
                    da:fd:52:06:ef:c3:bb:62:8a:56:ea:09:03:bc:cc:
                    1a:b2:70:6e:bc:cb:ca:b0:be:a6:e9:0a:ee:bc:ae:
                    39:f8:b6:36:fa:9d:49:05:5c:ed:60:51:7d:40:73:
                    0b:40:e7:df:c1:54:74:c1:84:a6:d2:64:21:74:35:
                    d7:0a:e3:d7:7c:26:04:84:e7:c7:2a:f9:8b:03:36:
                    ac:67:cc:2c:c8:fa:76:47:a0:95:d0:18:82:28:4e:
                    b0:4d
                Exponent: 65537 (0x10001)
        Attributes:
            (none)
            Requested Extensions:
    Signature Algorithm: sha256WithRSAEncryption
    Signature Value:
        2c:5f:c3:42:be:86:f8:6a:14:5f:5b:46:32:cf:34:81:1e:89:
        6a:22:70:11:28:79:8c:c3:35:5e:72:a4:68:2d:a8:4b:dd:43:
        f6:98:6a:54:a1:bf:14:d1:31:d0:56:e5:84:b0:cb:85:12:88:
        41:7a:d7:91:f7:c5:c4:71:11:56:ad:a2:b4:d0:70:13:f4:e3:
        fc:89:94:a4:ab:01:36:dd:69:59:1b:e9:e3:e1:6e:52:1c:7e:
        52:e0:46:6e:2c:f1:85:6a:29:91:f8:af:ab:99:97:5f:cd:cf:
        35:85:ba:fb:f4:11:2e:f5:4c:a6:04:f7:a8:1d:bc:c7:d5:61:
        39:ad:fa:b3:da:e0:3c:69:03:a8:16:08:03:2f:72:b8:36:41:
        e2:a5:67:53:c4:ad:f1:94:16:07:5f:de:8a:18:20:2c:5f:f8:
        e9:31:5d:14:4d:a4:ae:ed:14:b2:dd:a7:34:69:06:99:11:d9:
        47:2d:20:fa:d7:bb:04:6e:65:00:b3:d9:16:c8:e4:9d:d1:24:
        ae:27:72:b8:8b:c0:2e:de:52:94:b4:e4:8e:b9:13:0b:15:ae:
        d0:13:4f:10:bd:77:c2:bb:a9:9d:fa:ef:41:9b:2a:32:3f:8b:
        c7:f5:b1:c1:fb:ff:15:89:a5:1b:81:f5:0d:4d:93:bf:34:e5:
        12:24:b0:60

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ openssl x509 -in b77.csr -out b77.crt -req -signkey b77.key -days 365
Certificate request self-signature ok
subject=C = IN, ST = MH, L = KOP, O = KIT, OU = CSE, CN = b77, emailAddress = b77@gmail.com

ubuntu@ubuntu-HP-Pro-Tower-400-G9-PCI-Desktop-PC:~/B77_Openssl$ dir
b77.crt  b77.csr  b77.key  b77_public.key


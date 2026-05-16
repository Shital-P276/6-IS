┌──(student㉿csel2pc45)-[~]
└─$ ^[[200~sudo apt update
zsh: bad pattern: ^[[200~sudo
                                                                                            
┌──(student㉿csel2pc45)-[~]
└─$ sudo apt update                          
sudo apt install clamav clamav-daemon -y
[sudo] password for student: 
Get:1 http://elmirror.cl/kali kali-rolling InRelease [34.0 kB]
Get:2 http://elmirror.cl/kali kali-rolling/main amd64 Packages [20.9 MB]
Get:3 http://elmirror.cl/kali kali-rolling/main amd64 Contents (deb) [52.9 MB]             
Get:4 http://elmirror.cl/kali kali-rolling/contrib amd64 Packages [116 kB]                 
Get:5 http://elmirror.cl/kali kali-rolling/contrib amd64 Contents (deb) [259 kB]           
Get:6 http://elmirror.cl/kali kali-rolling/non-free amd64 Packages [188 kB]                
Get:7 http://elmirror.cl/kali kali-rolling/non-free-firmware amd64 Packages [15.4 kB]      
Fetched 74.4 MB in 20s (3,630 kB/s)                                                        
2942 packages can be upgraded. Run 'apt list --upgradable' to see them.
clamav is already the newest version (1.4.4+dfsg-1).
clamav-daemon is already the newest version (1.4.4+dfsg-1).
The following packages were automatically installed and are no longer required:
  icu-devtools  libicu-dev
Use 'sudo apt autoremove' to remove them.

Summary:
  Upgrading: 0, Installing: 0, Removing: 0, Not Upgrading: 2942
                                                                                            
┌──(student㉿csel2pc45)-[~]
└─$ ^[[200~sudo systemctl stop clamav-freshclam
zsh: bad pattern: ^[[200~sudo
                                                                                            
┌──(student㉿csel2pc45)-[~]
└─$ sudo freshclam
sudo systemctl start clamav-freshclam^[[201~Connecting via 172.16.100.252
Sat May 16 17:47:56 2026 -> ClamAV update process started at Sat May 16 17:47:56 2026
WARNING: Sat May 16 17:47:56 2026 -> Can't query current.cvd.clamav.net
WARNING: Sat May 16 17:47:56 2026 -> Invalid DNS reply. Falling back to HTTP mode.
Sat May 16 17:47:56 2026 -> Trying to retrieve CVD header from https://database.clamav.net/daily.cvd
Time:    0.1s, ETA:    0.0s [========================>]       512B/512B
Sat May 16 17:47:56 2026 -> OK
Sat May 16 17:47:56 2026 -> daily database available for update (local version: 27999, remote version: 28002)
Current database is 3 versions behind.
Downloading database patch # 28000...
Time:    0.1s, ETA:    0.0s [========================>]    2.31KiB/2.31KiB
Downloading database patch # 28001...
Time:    0.0s, ETA:    0.0s [========================>]    1.66KiB/1.66KiB
Downloading database patch # 28002...
Time:    0.0s, ETA:    0.0s [========================>]       787B/787B
Sat May 16 17:47:56 2026 -> Testing database: '/var/lib/clamav/tmp.9bf4ef85a1/clamav-f5b818204958a1e8373bfc9a0d80115e.tmp-daily.cld' ...
Sat May 16 17:47:58 2026 -> Database test passed.
Sat May 16 17:47:58 2026 -> daily.cld updated (version: 28002, sigs: 355454, f-level: 90, builder: svc.clamav-publisher)
Sat May 16 17:47:58 2026 -> Trying to retrieve CVD header from https://database.clamav.net/main.cvd
Sat May 16 17:47:58 2026 -> main.cvd database is up-to-date (version: 63, sigs: 3287027, f-level: 90, builder: tomjudge)
Sat May 16 17:47:58 2026 -> Trying to retrieve CVD header from https://database.clamav.net/bytecode.cvd
Sat May 16 17:47:58 2026 -> bytecode.cvd database is up-to-date (version: 339, sigs: 80, f-level: 90, builder: nrandolp)
WARNING: Sat May 16 17:47:58 2026 -> Clamd was NOT notified: Can't connect to clamd through /var/run/clamav/clamd.ctl: No such file or directory
                                                                                            
┌──(student㉿csel2pc45)-[~]
└─$ sudo systemctl stop clamav-freshclam
                                                                                                
┌──(student㉿csel2pc45)-[~]
└─$ sudo freshclam
Connecting via 172.16.100.252
Sat May 16 17:48:53 2026 -> ClamAV update process started at Sat May 16 17:48:53 2026
WARNING: Sat May 16 17:48:53 2026 -> Can't query current.cvd.clamav.net
WARNING: Sat May 16 17:48:53 2026 -> Invalid DNS reply. Falling back to HTTP mode.
Sat May 16 17:48:53 2026 -> Trying to retrieve CVD header from https://database.clamav.net/daily.cvd
Sat May 16 17:48:53 2026 -> daily.cld database is up-to-date (version: 28002, sigs: 355454, f-level: 90, builder: svc.clamav-publisher)
Sat May 16 17:48:53 2026 -> Trying to retrieve CVD header from https://database.clamav.net/main.cvd
Sat May 16 17:48:53 2026 -> main.cvd database is up-to-date (version: 63, sigs: 3287027, f-level: 90, builder: tomjudge)
Sat May 16 17:48:53 2026 -> Trying to retrieve CVD header from https://database.clamav.net/bytecode.cvd
Sat May 16 17:48:53 2026 -> bytecode.cvd database is up-to-date (version: 339, sigs: 80, f-level: 90, builder: nrandolp)
                                                                                                
┌──(student㉿csel2pc45)-[~]
└─$ sudo systemctl start clamav-freshclam
                                                                                                
┌──(student㉿csel2pc45)-[~]
└─$ echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*' > eicar.com
                                                                                                
┌──(student㉿csel2pc45)-[~]
└─$ mkdir malware_lab && cd malware_lab        
mkdir: cannot create directory ‘malware_lab’: File exists
                                                                                                
┌──(student㉿csel2pc45)-[~]
└─$ cd malware_lab
                                                                                                
┌──(student㉿csel2pc45)-[~/malware_lab]
└─$ echo 'X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*' > eicar.com
                                                                                                
┌──(student㉿csel2pc45)-[~/malware_lab]
└─$ clamscan -r .
Loading:     4s, ETA:   0s [========================>]    3.63M/3.63M sigs       
Compiling:   2s, ETA:   0s [========================>]       41/41 tasks 

/home/student/malware_lab/normal.txt: OK
/home/student/malware_lab/eicar.com: Eicar-Signature FOUND

----------- SCAN SUMMARY -----------
Known viruses: 3627862
Engine version: 1.4.4
Scanned directories: 2
Scanned files: 2
Infected files: 1
Data scanned: 0.00 MB
Data read: 0.00 MB (ratio 0.00:1)
Time: 5.403 sec (0 m 5 s)
Start Date: 2026:05:16 17:50:17
End Date:   2026:05:16 17:50:23
                                                                                                
┌──(student㉿csel2pc45)-[~/malware_lab]
└─$ 

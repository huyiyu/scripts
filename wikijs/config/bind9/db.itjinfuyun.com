$TTL 1D
@       IN SOA  jinfuyun.com. email.com. (
                                        0       ; serial
                                        1D      ; refresh
                                        1H      ; retry
                                        1W      ; expire
                                        3H )    ; minimum
;
@ IN	NS	localhost.
wiki   IN  A     121.5.50.204

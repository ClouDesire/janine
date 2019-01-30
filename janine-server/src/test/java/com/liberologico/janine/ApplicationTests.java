package com.liberologico.janine;

import com.liberologico.janine.entities.Address;
import com.liberologico.janine.entities.Holder;
import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.entities.Line;
import com.liberologico.janine.entities.Person;
import com.liberologico.janine.entities.Price;
import com.liberologico.janine.entities.Recipient;
import com.liberologico.janine.upload.StoreService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.Arrays;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
abstract class ApplicationTests
{
    private static final String ROOT = "http://localhost";
    static final String PREFIX = "TEST";

    @LocalServerPort
    int port;

    InvoiceService service;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private StoreService storeService;

    @Value( "${blob.containers-prefix}" )
    protected String containersPrefix;

    @Before
    @After
    public void reset()
    {
        service = new InvoiceClient( ROOT + ":" + port ).getService();

        Jedis jedis = new Jedis( jedisConnectionFactory.getHostName(), jedisConnectionFactory.getPort() );
        jedis.flushAll();
        jedis.close();

        final String container = containersPrefix + PREFIX;
        storeService.flushContainer( container );
    }

    Invoice getInvoice()
    {
        return getInvoice(
                new Line()
                        .setDescription( "Riga 1" )
                        .setPrice( new Price().setPrice( BigDecimal.TEN ).setVAT( BigDecimal.ONE ) ),
                new Line()
                        .setDescription( "Riga 2" )
                        .setPrice( new Price().setPrice( BigDecimal.ONE ).setVAT( BigDecimal.ONE ) ) );
    }

    Invoice getInvoice( Line... lines )
    {
        final Person holder = new Holder()
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) )
                .setCompanyName( "Caffè Toraldo" )
                .setTaxCode( "CFTGNN" );
        final Person recipient = new Recipient()
                .setFirstName( "Brebuzio" )
                .setLastName( "Sfanti" )
                .setEmail( "di@tu.ma" )
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) );
        return new Invoice()
                .setHolder( (Holder) holder )
                .setRecipient( (Recipient) recipient )
                .setCurrency( "EUR" )
                .setLines( Arrays.asList( lines ) );
    }
}

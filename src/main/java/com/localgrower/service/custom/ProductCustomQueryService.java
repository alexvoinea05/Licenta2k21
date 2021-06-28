package com.localgrower.service.custom;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrixRow;
import com.localgrower.domain.*;
import com.localgrower.domain.custom.AddressPair;
import com.localgrower.domain.custom.AlgPackage.GraphDFS;
import com.localgrower.domain.custom.AlgPackage.Node;
import com.localgrower.repository.AppUserRepository;
import com.localgrower.repository.ProductRepository;
import com.localgrower.repository.UserRepository;
import com.localgrower.repository.custom.ProductCustomRepository;
import com.localgrower.security.SecurityUtils;
import com.localgrower.service.ProductQueryService;
import com.localgrower.service.criteria.ProductCriteria;
import com.localgrower.service.dto.ProductDTO;
import com.localgrower.service.dto.custom.*;
import com.localgrower.service.mapper.AppUserMapper;
import com.localgrower.service.mapper.ProductMapper;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
public class ProductCustomQueryService extends QueryService<Product> {

    private static final String API_KEY = "AIzaSyB7cRSTxs-r921iFErTu-QBUzICUFdmC4g";

    private final Logger log = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final UserRepository userRepository;

    private final ProductCustomRepository productCustomRepository;

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public ProductCustomQueryService(
        ProductRepository productRepository,
        ProductMapper productMapper,
        UserRepository userRepository,
        ProductCustomRepository productCustomRepository,
        AppUserRepository appUserRepository,
        AppUserMapper appUserMapper
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.userRepository = userRepository;
        this.productCustomRepository = productCustomRepository;
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findByIdCategory(ProductCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.findAll(specification, page).map(productMapper::toDto);
    }

    public List<ProductDTO> getAllProductsWantedAfterAlgorithm(
        List<ProductOverallAddressPairDistanceTime> productOverallAddressPairDistanceTimeList
    ) {
        List<Long> idProductList = new ArrayList<>();
        HashMap<String, Integer> nodeIntegerHashMap = new HashMap<>();
        List<Node> nodeList = new ArrayList<>();
        Integer count = 0;
        List<String> listaNumeProduse = new ArrayList<>();
        for (ProductOverallAddressPairDistanceTime productOverallAddressPairDistanceTime : productOverallAddressPairDistanceTimeList) {
            if (!(idProductList.contains(productOverallAddressPairDistanceTime.getIdProdus()))) {
                Node node = new Node(
                    productOverallAddressPairDistanceTime.getIdProdus().toString(),
                    productOverallAddressPairDistanceTime.getNumeProdus()
                );
                nodeIntegerHashMap.put(productOverallAddressPairDistanceTime.getIdProdus().toString(), count);
                nodeList.add(node);
                count++;
                idProductList.add(productOverallAddressPairDistanceTime.getIdProdus());
                log.debug("Hash : {}", nodeIntegerHashMap.toString());
                //                log.debug("COUNT : {}", count);
                //                log.debug("Node curent : {}", node);
            }

            // log.debug("NodeList:{}", nodeList.size());
            if (!listaNumeProduse.contains(productOverallAddressPairDistanceTime.getNumeProdus())) {
                listaNumeProduse.add(productOverallAddressPairDistanceTime.getNumeProdus());
            }
        }

        log.debug("CountProduse: {}", listaNumeProduse.size());

        for (int i = 0; i < productOverallAddressPairDistanceTimeList.size(); i++) {
            AddressPair addressPair = productOverallAddressPairDistanceTimeList.get(i).getAddressPair();
            AddressPair addressPairReverse = new AddressPair(addressPair.second, addressPair.first);
            GasPriceHandler gasPriceHandler = new GasPriceHandler("http://www.pretbenzina.ro/");
            String gasPriceTrim = gasPriceHandler.getGasPrice().trim();
            BigDecimal gasPrice = new BigDecimal(gasPriceTrim);
            for (int j = 0; j < productOverallAddressPairDistanceTimeList.size(); j++) {
                if (
                    (
                        !(
                            productOverallAddressPairDistanceTimeList
                                .get(i)
                                .getIdProdus()
                                .equals(productOverallAddressPairDistanceTimeList.get(j).getIdProdus())
                        )
                    ) &&
                    (
                        !(
                            productOverallAddressPairDistanceTimeList
                                .get(i)
                                .getNumeProdus()
                                .equals(productOverallAddressPairDistanceTimeList.get(j).getNumeProdus())
                        )
                    )
                ) { //&&
                    //((productOverallAddressPairDistanceTimeList.get(j).getAddressPair().equals(addressPairReverse)) ||
                    //   (productOverallAddressPairDistanceTimeList.get(j).getAddressPair().equals(addressPair)))){
                    List<String> distanceList = Arrays.asList(
                        productOverallAddressPairDistanceTimeList.get(j).getDistanceTimeDTO().getDistance().split(" ")
                    );
                    BigDecimal distanceBigDecimal = new BigDecimal(distanceList.get(0));
                    BigDecimal costCarburant = new BigDecimal("0");
                    costCarburant = distanceBigDecimal.multiply(BigDecimal.valueOf(8)).multiply(gasPrice).divide(BigDecimal.valueOf(100));
                    BigDecimal costDrum = new BigDecimal("0");
                    costDrum = productOverallAddressPairDistanceTimeList.get(j).getPretTotal().add(costCarburant);
                    // productOverallAddressPairDistanceTimeList.get(j).setPretTotal(costDrum);
                    log.debug("Cost carburant: {}", costCarburant);
                    log.debug("Cost drum: {}", costDrum);
                    //Node node = new Node(productOverallAddressPairDistanceTimeList.get(j).getIdProdus().toString(),productOverallAddressPairDistanceTimeList.get(j).getNumeProdus());
                    Integer positionInNodeList = nodeIntegerHashMap.get(
                        productOverallAddressPairDistanceTimeList.get(i).getIdProdus().toString()
                    );
                    //log.debug("Position in NODE list: {}",positionInNodeList);
                    Integer positionDestinationInNodeList = nodeIntegerHashMap.get(
                        productOverallAddressPairDistanceTimeList.get(j).getIdProdus().toString()
                    );
                    log.debug(
                        "Nod Id produs:{}",
                        nodeIntegerHashMap.get(productOverallAddressPairDistanceTimeList.get(j).getIdProdus().toString())
                    );
                    nodeList.get(positionInNodeList).addDestination(nodeList.get(positionDestinationInNodeList), costDrum);
                    //log.debug("NODE LIST : {} ",nodeList.toString());
                }
            }
        }
        log.debug("Node List: {}", nodeList.toString());
        log.debug("Node List Size: {}", nodeList.size());
        GraphDFS graph = new GraphDFS();

        for (Node node : nodeList) {
            graph.addNode(node);
            log.debug("NODURIIII : { }", node.toString());
            for (Map.Entry<Node, BigDecimal> entry : node.getAdjacentNodes().entrySet()) {
                Node key = entry.getKey();
                BigDecimal value = entry.getValue();
                log.debug("Keyyy:{}", key);
                log.debug("Valuee:{}", value);
            }
        }
        //Node node = new Node(Long.valueOf(0).toString(),"");
        int numarProduse = listaNumeProduse.size();

        boolean visited[] = new boolean[10000];

        graph.DFSUtil(nodeList.get(0), BigDecimal.valueOf(0), visited, new HashSet<>(), numarProduse);

        BigDecimal cost = graph.getCost();

        Set<Node> nodeSet = new HashSet<>(graph.getCorrectPath());
        log.debug("CORRECT : {}", graph.getCorrectPath().toString());
        log.debug("NODE SET : {}", nodeSet);
        List<Long> idList = new ArrayList<>();

        for (Node nodeIt : nodeSet) {
            idList.add(Long.valueOf(nodeIt.getId()));
        }
        List<ProductDTO> productDTOList = new ArrayList<>();
        if (!idList.isEmpty()) {
            List<Product> productList = productRepository.findAllById(idList);
            productDTOList = productMapper.toDto(productList);
            return productDTOList;
        }

        return productDTOList;
    }

    //facut 07.06.2021; 06:03AM //de verificat inca o data
    public List<ProductOverallAddressPairDistanceTime> getProductOverallAddressPairDistanceTimeList(
        List<ProductWantedOverall> productWantedOverallList
    ) {
        List<ProductOverallAddressPairDistanceTime> productOverallAddressPairDistanceTimeList = new ArrayList<>();
        List<AddressPair> addressPairList = new ArrayList<>();
        for (ProductWantedOverall productWantedOverall : productWantedOverallList) {
            addressPairList.add(productWantedOverall.getAddressPair());
        }

        HashMap<AddressPair, DistanceTimeDTO> addressPairDistanceTimeDTOHashMap = getAddressPairDistanceAndTime(addressPairList);

        for (ProductWantedOverall productWantedOverall : productWantedOverallList) {
            ProductOverallAddressPairDistanceTime productOverallAddressPairDistanceTime = new ProductOverallAddressPairDistanceTime();

            productOverallAddressPairDistanceTime.setAddressPair(productWantedOverall.getAddressPair());
            productOverallAddressPairDistanceTime.setIdProdus(productWantedOverall.getIdProdus());
            productOverallAddressPairDistanceTime.setNumeProdus(productWantedOverall.getNumeProdus());
            productOverallAddressPairDistanceTime.setPretTotal(productWantedOverall.getPretTotal());
            productOverallAddressPairDistanceTime.setDistanceTimeDTO(
                addressPairDistanceTimeDTOHashMap.get(productWantedOverall.getAddressPair())
            );

            productOverallAddressPairDistanceTimeList.add(productOverallAddressPairDistanceTime);
        }

        return productOverallAddressPairDistanceTimeList;
    }

    public HashMap<AddressPair, DistanceTimeDTO> getAddressPairDistanceAndTime(List<AddressPair> addressPairList) {
        HashMap<AddressPair, DistanceTimeDTO> addressPairDistanceTimeDTOHashMap = new HashMap<>();

        for (AddressPair addressPair : addressPairList) {
            AddressPair addressPairInversata = new AddressPair(addressPair.second, addressPair.first);
            if (
                (!(addressPairDistanceTimeDTOHashMap.containsKey(addressPair))) &&
                (!addressPairDistanceTimeDTOHashMap.containsKey(addressPairInversata))
            ) {
                DistanceTimeDTO distanceTimeDTO = getDistanceTime(addressPair.first.toString(), addressPair.second.toString());
                addressPairDistanceTimeDTOHashMap.put(addressPair, distanceTimeDTO);
                addressPairDistanceTimeDTOHashMap.put(addressPairInversata, distanceTimeDTO);
            }
        }
        return addressPairDistanceTimeDTOHashMap;
    }

    public DistanceTimeDTO getDistanceTime(String punctStart, String punctFinal) {
        GeoApiContext contextGeoApi = new GeoApiContext().setApiKey(API_KEY);

        DistanceMatrixRow[] rows = DistanceMatrixApi
            .newRequest(contextGeoApi)
            .origins(punctStart)
            .destinations(punctFinal)
            .awaitIgnoreError()
            .rows;

        DistanceTimeDTO distanceTimeDTO = new DistanceTimeDTO();
        log.debug("Origins: {}", punctStart);
        log.debug("Destinations: {} ", punctFinal);
        distanceTimeDTO.setDistance(rows[0].elements[0].distance.toString());
        distanceTimeDTO.setTime(rows[0].elements[0].duration.toString());

        return distanceTimeDTO;
    }

    public List<ProductWantedOverall> getProductWantedOverallList(List<ProductAndPriceDTO> productDTOS) {
        //HashMap<AddressPair,ProductWantedOverall> addressPairStringHashMap = new HashMap<>();
        List<ProductWantedOverall> productWantedOverallWithAddressPairList = new ArrayList<>();
        List<AddressPair> addressPairList = new ArrayList<>();
        User userCurrent = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).get();
        AppUser appUserCurrent = appUserRepository.findAppUserByUserId(userCurrent.getId()).get();
        //AppUser appUserCurrent = appUserRepository.findAppUserByUserId(Long.valueOf(1)).get();
        String localitateJudetCurrentUser = getAddressUser(appUserCurrent);
        List<String> addressBetweenGrowers = new ArrayList<>();
        for (ProductAndPriceDTO productAndPriceDTO : productDTOS) {
            AppUser appUser = appUserRepository.findById(productAndPriceDTO.getIdGrower().getIdAppUser()).get();
            String localitateJudetCurrentGrower = getAddressUser(appUser);
            AddressPair addressPair = new AddressPair(localitateJudetCurrentUser, localitateJudetCurrentGrower);
            addressPairList.add(addressPair);
            AddressPair addressPairNodStart = new AddressPair(localitateJudetCurrentGrower, localitateJudetCurrentUser);
            ProductWantedOverall productWantedOverall = new ProductWantedOverall(
                productAndPriceDTO.getIdProduct(),
                productAndPriceDTO.getName(),
                productAndPriceDTO.getPretTotal(),
                addressPair
            );
            ProductWantedOverall productWantedOverallNode0 = new ProductWantedOverall(
                Long.valueOf(0),
                "",
                BigDecimal.valueOf(0),
                addressPairNodStart
            );
            productWantedOverallWithAddressPairList.add(productWantedOverallNode0);
            productWantedOverallWithAddressPairList.add(productWantedOverall);
            addressBetweenGrowers.add(localitateJudetCurrentGrower);
            for (int i = 0; i < addressBetweenGrowers.size() - 1; i++) {
                if ((!(productAndPriceDTO.getName().equals(productDTOS.get(i).getName())))) {
                    AddressPair addressPair1 = new AddressPair(addressBetweenGrowers.get(i), localitateJudetCurrentGrower);
                    ProductWantedOverall productWantedOverall1 = new ProductWantedOverall(
                        productAndPriceDTO.getIdProduct(),
                        productAndPriceDTO.getName(),
                        productAndPriceDTO.getPretTotal(),
                        addressPair1
                    );
                    productWantedOverallWithAddressPairList.add(productWantedOverall1);
                }
            }
        }
        return productWantedOverallWithAddressPairList;
    }

    public String getAddressUser(AppUser appUser) {
        String addressCurrentUser = appUser.getAdress();
        List<String> addressAsStringList = Arrays.asList(addressCurrentUser.split(",", -1));
        List<String> judetArray = Arrays.asList(addressAsStringList.get(0).split("\\W"));
        String judet = "";
        //if(!judetArray.isEmpty()) {
        judet = judetArray.get(2);
        log.debug("Judettt:{}", judet);
        //}
        List<String> localitateArray = Arrays.asList(addressAsStringList.get(1).split("\\W"));
        log.debug("LocccArr:{}", localitateArray);
        String localitate = "";
        if (localitateArray.size() > 4) {
            if (localitateArray.get(4).equals("Str.") || localitateArray.get(4).equals("Str") || localitateArray.get(4).equals("Strada")) {
                localitate = localitateArray.get(3);
            } else {
                localitate = localitateArray.get(3) + " " + localitateArray.get(4);
            }
        } else {
            localitate = localitateArray.get(3);
        }
        log.debug("Loccc:{}", localitate);
        String localitateJudetCurrentUser = judet + "," + localitate;

        return localitateJudetCurrentUser;
    }

    public Optional<List<ProductAndPriceDTO>> findAllWantedProducts(List<ProductSearchDTO> productSearchDTOS) {
        List<User> growerList = new ArrayList<>();
        //HashMap<ProductDTO,BigDecimal> productWanted = new HashMap<>();
        List<ProductAndPriceDTO> productWanted = new ArrayList<>();
        for (ProductSearchDTO productSearchDTO : productSearchDTOS) {
            if (
                productCustomRepository
                    .findAllProductsWantedByCustomer(productSearchDTO.getNumeProdus(), productSearchDTO.getQuantity())
                    .isPresent()
            ) {
                List<Product> productList = productCustomRepository
                    .findAllProductsWantedByCustomer(productSearchDTO.getNumeProdus(), productSearchDTO.getQuantity())
                    .get();
                for (Product product : productList) {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO = productMapper.toDto(product);
                    //product.setName(productSearchDTO.getNumeProdus());
                    BigDecimal pret = product.getPrice().multiply(productSearchDTO.getQuantity());
                    ProductAndPriceDTO productAndPriceDTO = new ProductAndPriceDTO();
                    productAndPriceDTO.setIdProduct(productDTO.getIdProduct());
                    productAndPriceDTO.setPretTotal(pret);
                    productAndPriceDTO.setIdGrower(productDTO.getIdGrower());
                    productAndPriceDTO.setIdCategory(productDTO.getIdCategory());
                    productAndPriceDTO.setPrice(productDTO.getPrice());
                    productAndPriceDTO.setCreatedAt(productDTO.getCreatedAt());
                    productAndPriceDTO.setModifiedAt(productDTO.getModifiedAt());
                    productAndPriceDTO.setStock(productDTO.getStock());
                    productAndPriceDTO.setName(productSearchDTO.getNumeProdus());

                    productWanted.add(productAndPriceDTO);
                }
            }
        }
        Optional<List<ProductAndPriceDTO>> productAndPriceDTOList = Optional.of(productWanted);
        return productAndPriceDTOList;
    }

    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getIdCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getIdCategoryId(),
                            root -> root.join(Product_.idCategory, JoinType.LEFT).get(Category_.idCategory)
                        )
                    );
            }
        }
        return specification;
    }
}

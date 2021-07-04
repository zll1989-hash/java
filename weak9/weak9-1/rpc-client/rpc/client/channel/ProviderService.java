

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderService implements Serializable {
    private String serverIp;
    private int serverPort;
    private int networkPort;

    private long timeout;
    // the weight of service provider
    private int weight;
}

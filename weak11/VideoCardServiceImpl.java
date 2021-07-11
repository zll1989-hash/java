

@Service
public class VideoCardServiceImpl implements VideoCardService {

    @Autowired
    private VideoCardDao videoCardDao;

    @Override
    public List<VideoCardDO> list() {

        return videoCardDao.list();
    }
}

package org.lemanoman.filerepository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lemanoman.filerepository.FileExtraInfo;
import org.lemanoman.filerepository.data.BucketConfig;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DownloadService {
    public final LocationService locationService;

    public DownloadService(LocationService locationService) {
        this.locationService = locationService;
    }


}
